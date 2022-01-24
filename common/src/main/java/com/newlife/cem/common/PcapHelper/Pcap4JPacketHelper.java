/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.newlife.cem.common.PcapHelper;

import org.pcap4j.packet.EthernetPacket;
import org.pcap4j.packet.IpV4Packet;
import org.pcap4j.packet.Packet;
import org.pcap4j.packet.namednumber.EtherType;
import org.pcap4j.util.ByteArrays;

/**
 *
 * @author hung_
 */
public class Pcap4JPacketHelper {

    public static PcapPacketIp4Info parsePcap4jPacket(Packet packet) {
        try {
            PcapPacketIp4Info packetIp4Info=new PcapPacketIp4Info();
            packetIp4Info.packet_len=packet.length();
            EthernetPacket ethPacket = packet.get(EthernetPacket.class);
            //fix capture on phone
            if(ethPacket==null){
                ethPacket=EthernetPacket.newPacket(packet.getRawData(), 2, packet.getRawData().length-2);
            }
            
            packetIp4Info.ethHeader=ethPacket.getHeader();
            EtherType etherType = ethPacket.getHeader().getType();
            if (etherType.value() == (short) 0x8864) { // pppoe sturct
                int ppp_proto_offset = 6;
                byte[] ethPayloadData = ethPacket.getPayload().getRawData();
                short ppp_proto = ByteArrays.getShort(ethPayloadData, ppp_proto_offset);
                if (ppp_proto == (short) 0x0021) {
                    int ipv4_offset = 8;
                    IpV4Packet ipV4Packet = IpV4Packet.newPacket(ethPayloadData, ipv4_offset, ethPayloadData.length - ipv4_offset);
                    packetIp4Info.ipV4Packet=ipV4Packet;
                }
            }else if(etherType.value() == (short) 0x0800){ //normal struct
                IpV4Packet ipV4Packet =packet.get(IpV4Packet.class);
                packetIp4Info.ipV4Packet=ipV4Packet;
            }
            return packetIp4Info;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
