/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.newlife.cem.common.PcapHelper;

import java.sql.Timestamp;
import org.pcap4j.packet.EthernetPacket;
import org.pcap4j.packet.IpV4Packet;

/**
 *
 * @author hung_
 */
public class PcapPacketIp4Info {
    public Timestamp ts;
    public int packet_len;
    public EthernetPacket.EthernetHeader ethHeader;
    public IpV4Packet ipV4Packet;
}
