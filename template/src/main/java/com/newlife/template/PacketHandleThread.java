package com.newlife.template;

import java.util.Arrays;
// import java.net.Inet4Address;
import java.util.Date;
import java.util.HashMap;
// import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
// import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.LinkedBlockingQueue;

import com.newlife.cem.common.PcapHelper.PacketFlowInfo;
import com.newlife.cem.common.PcapHelper.PacketInfo;
import com.newlife.cem.common.PcapHelper.PcapPacketIp4Info;
import com.newlife.cem.common.PcapHelper.TcpPacketInfo;
import com.newlife.cem.common.PcapHelper.UdpPacketInfo;

import org.pcap4j.packet.IpV4Packet;
import org.pcap4j.packet.TcpPacket;
import org.pcap4j.packet.UdpPacket;
import org.pcap4j.util.MacAddress;

public class PacketHandleThread extends Thread {

    private LinkedBlockingQueue<PcapPacketIp4Info> packetQueue = new LinkedBlockingQueue<>();
    public HashMap<PacketFlowInfo, FlowHandle> flowHandleMap = new HashMap<>();
    // HashMap<Long, ViberMsgPacketHandle> viberMsgHandleList = new HashMap<>();
    // MapLimitBySize<PacketFlowInfo, byte[]> listInitPacketMap = new
    // MapLimitBySize<>(10000);
    int worker_id;
    int num_thread = App.NUMBER_THREAD;
    long flow_idx = 0;
    byte[] brasMac;

    public PacketHandleThread(int worker_id, byte[] brasMac) {
        this.worker_id = worker_id;
        this.flow_idx = worker_id;
        this.brasMac = brasMac;
    }

    @Override
    public void run() {
        // CheckFlowFinishThread checkFlowFinishThread=new
        // CheckFlowFinishThread(flowHandleMap);
        // checkFlowFinishThread.start();
        // int count = 0;
        long last_check_flow_finish = -1;
        while (true) {
            try {
                PcapPacketIp4Info packetIp4Info = packetQueue.poll();
                // count++;
                long curr_ts = System.currentTimeMillis();
                if (last_check_flow_finish < 0) {
                    last_check_flow_finish = curr_ts;
                }
                if (packetIp4Info != null) {
                    // System.out.println("have packet");
                    packetHandle(packetIp4Info);
                } else {
                    Thread.sleep(10);
                    // System.out.println("check finish 1");
                    boolean all_finish = checkFlowFinish();
                    if (App.isReadPcapFinish && all_finish) {
                        // checkFlowFinishThread.isReadFinish=true;
                        break;
                    }
                }
                if (curr_ts >= last_check_flow_finish + 20000) {
                    // System.out.println("check finish 2");
                    checkFlowFinish();
                    last_check_flow_finish = curr_ts;
                    // count = 0;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        try {
            // checkFlowFinishThread.join();
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("Worker thread ended");
    }

    public void addPacket(PcapPacketIp4Info packetIp4Info) {
        packetQueue.add(packetIp4Info);
    }

    //function to process packet and add some filter
    private void packetHandle(PcapPacketIp4Info packetIp4Info) {
        try {
            //
            // System.out.println(packetIp4Info.ts+" ");
            // Thread.sleep(50);
            IpV4Packet.IpV4Header ipV4Header = packetIp4Info.ipV4Packet.getHeader();
            int l4_proto = ipV4Header.getProtocol().value() & 0xff;

            if (l4_proto != 17 && l4_proto != 06) { // not UDP -> return
                return;
            }

            int direction = -1;
            MacAddress mac_src = packetIp4Info.ethHeader.getSrcAddr();
            MacAddress mac_dst = packetIp4Info.ethHeader.getDstAddr();
            boolean dst_mac_ok = false;
            if (Arrays.equals(brasMac, mac_src.getAddress())) {
                direction = 0;// downlink
                dst_mac_ok = true;
            }
            if (Arrays.equals(brasMac, mac_dst.getAddress())) {
                direction = 1;// uplink
                dst_mac_ok = true;
            }
            if (!dst_mac_ok) {
                // System.out.println(" cannot matching Mac ");
                return;
            }

            byte[] ipPayload = packetIp4Info.ipV4Packet.getPayload().getRawData();
            Integer srcPort = null;
            Integer dstPort = null;
            PacketInfo packetInfo = null;

            // already remove tcp in capture filter
            if (l4_proto == 06) {// tcp
                if (ipPayload.length < 13) {
                    return;
                }
                int tcp_header_len = ((ipPayload[12] & 0xf0) >> 4);
                if (tcp_header_len == 0) {
                    System.out.println("Bogus TCP header length " + ipPayload[12]);
                    return;
                }
                TcpPacket tcpPacket = TcpPacket.newPacket(ipPayload, 0, ipPayload.length);
                TcpPacket.TcpHeader tcpHeader = tcpPacket.getHeader();
                srcPort = tcpHeader.getSrcPort().valueAsInt();
                dstPort = tcpHeader.getDstPort().valueAsInt();

                TcpPacketInfo tcpPacketInfo = new TcpPacketInfo();
                tcpPacketInfo.tcpPacket = tcpPacket;
                packetInfo = tcpPacketInfo;
            }
            if (l4_proto == 17) {// udp
                UdpPacket udpPacket = UdpPacket.newPacket(ipPayload, 0, ipPayload.length);
                UdpPacket.UdpHeader udpHeader = udpPacket.getHeader();
                srcPort = udpHeader.getSrcPort().valueAsInt();
                dstPort = udpHeader.getDstPort().valueAsInt();

                UdpPacketInfo udpPacketInfo = new UdpPacketInfo();
                udpPacketInfo.udpPacket = udpPacket;
                packetInfo = udpPacketInfo;
            }

            if (srcPort == null || dstPort == null || packetInfo == null) {
                System.out.println("port null packet null");
                return;
            }
            

            Long ts_s = packetIp4Info.ts.getTime() / 1000;
            double ts_mili = ts_s + (double) packetIp4Info.ts.getNanos() / 1000000000;
            packetInfo.timestamp = ts_mili;
            packetInfo.direction = direction;
            packetInfo.packet_len = packetIp4Info.packet_len;
            packetInfo.ethernetHeader = packetIp4Info.ethHeader;
            packetInfo.ipV4Header = ipV4Header;

            PacketFlowInfo flowInfo = new PacketFlowInfo();

            flowInfo.ts_idx = Math.round(packetInfo.timestamp);

            flowInfo.flow_direct = direction;
            flowInfo.macSrc = mac_src;
            flowInfo.macDst = mac_dst;
            flowInfo.ipSrc = packetInfo.ipV4Header.getSrcAddr();
            flowInfo.ipDst = packetInfo.ipV4Header.getDstAddr();
            flowInfo.l4Proto = l4_proto;
            flowInfo.srcPort = srcPort;
            flowInfo.dstPort = dstPort;

            if (flowHandleMap.containsKey(flowInfo)) {
                FlowHandle flowHandle = flowHandleMap.get(flowInfo);
                flowHandle.addPacket(packetInfo);
            } else {
                    flow_idx = flow_idx + num_thread;
                    flowInfo.flow_idx = flow_idx;
                    // System.out.println(flow_idx+" detect fb flow tcp");

                    MyPacketHandle flowHandle = new MyPacketHandle(flowInfo);
                    flowHandle.addPacket(packetInfo);
                    flowHandleMap.put(flowInfo, flowHandle);
            }

            // byte[] ipPayload = packetIp4Info.ipV4Packet.getPayload().getRawData();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean checkFlowFinish() {
        boolean all_flow_finish = true;
        int flow_timeout = 30;
        // System.out.println("map_size "+flowHandleMap.size());
        Date date = new Date();
        double current_ts = (double) date.getTime() / 1000;
        Iterator<Map.Entry<PacketFlowInfo, FlowHandle>> iter = flowHandleMap.entrySet().iterator();

        while (iter.hasNext()) {
            try {
                Map.Entry<PacketFlowInfo, FlowHandle> entry = iter.next();
                FlowHandle flowHandle = entry.getValue();
                if (flowHandle.last_add_pk_ts > 0 && current_ts - flowHandle.last_add_pk_ts > flow_timeout) {
                    // System.out.println(entry.getKey() + " finish " + current_ts + " " +
                    // flowHandle.last_add_pk_ts);
                    // excute flow end event
                    flowHandle.flowEndEvt();
                    iter.remove();
                } else {
                    all_flow_finish = false;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return all_flow_finish;
    }

}
