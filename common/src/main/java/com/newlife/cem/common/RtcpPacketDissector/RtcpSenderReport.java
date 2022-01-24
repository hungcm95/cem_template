/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.newlife.cem.common.RtcpPacketDissector;

/**
 *
 * @author hung_
 */
public class RtcpSenderReport extends RtcpPacketInfo {

    public RtcpSenderReport(RtcpPacketInfo rpi) {
        super.flags=rpi.flags;
        super.length_seqNum=rpi.length_seqNum;
        super.packet_type=rpi.packet_type;
        super.sender_ssrc=rpi.sender_ssrc;
    }
    
    
    public int MSW_ts;
    public int LSW_ts;
    public int RTP_ts;
    public int sender_pk_count;
    public int sender_oct_count;
    public Source_n[] source_ns;
}
