/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.newlife.cem.common.PcapHelper;

import org.pcap4j.packet.EthernetPacket;
import org.pcap4j.packet.IpV4Packet;

/**
 *
 * @author hung_
 */
public class PacketInfo {
    public int direction;
    public double timestamp;
    public int packet_len;
    public EthernetPacket.EthernetHeader ethernetHeader;
    public IpV4Packet.IpV4Header ipV4Header;
}
