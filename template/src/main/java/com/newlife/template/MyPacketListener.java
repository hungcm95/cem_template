package com.newlife.template;

import java.net.Inet4Address;

import com.newlife.cem.common.PcapHelper.Pcap4JPacketHelper;
import com.newlife.cem.common.PcapHelper.PcapPacketIp4Info;

import org.pcap4j.core.PacketListener;
import org.pcap4j.core.PcapHandle;
import org.pcap4j.packet.Packet;

public class MyPacketListener implements PacketListener {

    PcapHandle pcapHandle;

    public MyPacketListener(PcapHandle pcapHandle) {
        this.pcapHandle = pcapHandle;
    }

    @Override
    public void gotPacket(Packet packet) {
        PcapPacketIp4Info packetIp4Info = Pcap4JPacketHelper.parsePcap4jPacket(packet);
        packetIp4Info.ts = pcapHandle.getTimestamp();

        if (packetIp4Info.ipV4Packet == null) {
            return;
        }
        // System.out.println("AAAAAAAAAAAAAAAAAAA");
        Inet4Address ipSrc = packetIp4Info.ipV4Packet.getHeader().getSrcAddr();
        Inet4Address ipDst = packetIp4Info.ipV4Packet.getHeader().getDstAddr();

        int processIdx = Math.abs(ipSrc.hashCode() + ipDst.hashCode()) % App.NUMBER_THREAD;
        // System.out.println(ipSrc.getHostAddress()+" - "+ipDst.getHostAddress());
        // System.out.println("process_idx "+processIdx);
        PacketHandleThread handleThread = App.packetHandleWorkerList.get(processIdx);
        if (handleThread != null) {
            handleThread.addPacket(packetIp4Info);
        } else {
            System.out.println(" handleThread not found " + processIdx);
            System.exit(-1);
        }
    }

}
