package com.newlife.template;

import java.util.Date;

import com.newlife.cem.common.Constant;
import com.newlife.cem.common.PcapHelper.PacketFlowInfo;
import com.newlife.cem.common.PcapHelper.PacketInfo;
import com.newlife.cem.common.PcapHelper.TcpPacketInfo;
import com.newlife.cem.common.PcapHelper.UdpPacketInfo;
import com.newlife.cem.common.TcpFlowHelper.TcpFlowHelper;

public class MyPacketHandle extends FlowHandle {

    public MyPacketHandle(PacketFlowInfo flowInfo) {
        super(flowInfo);
        // TODO Auto-generated constructor stub
    }

    @Override
    void addPacket(PacketInfo packetInfo) {

        // must have to check flow end
        Date date = new Date();
        super.last_add_pk_ts = (double) date.getTime() / 1000;

        //handle code
        if(packetInfo.getClass()==TcpPacketInfo.class){
            TcpPacketInfo tcpPacketInfo = (TcpPacketInfo) packetInfo;
            tcppacketHandle(tcpPacketInfo);
        }else if(packetInfo.getClass()==UdpPacketInfo.class){
            UdpPacketInfo udpPacketInfo=(UdpPacketInfo)packetInfo;
            udppacketHandle(udpPacketInfo);
        }
        
    }
    TcpFlowHelper tfh=new TcpFlowHelper(0.033634000);
    int retran_count=0;
    private void tcppacketHandle(TcpPacketInfo tpi) {
        int tcp_stat=tfh.getTcpPacketTransmitInfo(tpi);
        if(tcp_stat==Constant.TcpTransmitType.RETRANSMISSION){
            retran_count++;
            System.out.println(tpi.timestamp);
        }
        //code tcp packet here
        // System.out.println("tcp packet");

    }

    private void udppacketHandle(UdpPacketInfo upi) {
        //code udp packet here
    }
    
    @Override
    void flowEndEvt() {
        // flow end code here
        System.out.println("num_retran "+retran_count);
    }

}
