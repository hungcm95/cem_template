/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.newlife.cem.common.RtcpPacketDissector;

import java.util.LinkedList;
import java.util.List;
import org.pcap4j.util.ByteArrays;

/**
 *
 * @author hung_
 */
public class RtpPacketDissector {

    public static final int FIRST_RTCP_CONFLICT_PAYLOAD_TYPE = 64;
    public static final int LAST_RTCP_CONFLICT_PAYLOAD_TYPE = 95;

    public static List<RtpPacketInfo> parsePacket(byte[] payload, int offset) {
        List<RtpPacketInfo> rtpPackets = new LinkedList<>();
        try {
            if (offset >= payload.length) {
                return rtpPackets;
            }
            int packet_pos = offset;
            while (packet_pos < payload.length) {
                try {
                    offset = packet_pos;
                    RtpPacketInfo rpi = new RtpPacketInfo();
                    rpi.flags = payload[offset];
                    offset++;
                    int rtp_ver=(rpi.flags>>6)&0x03;
                    if(rtp_ver!=2){
                        
                        break;
                    }
                    rpi.packet_type = payload[offset];
                    offset++;
                    rpi.length_seqNum = ByteArrays.getShort(payload, offset);
                    offset = offset + 2;

                    int packet_type = rpi.packet_type & 0xff;
                    boolean marker_set = (packet_type & 0x00000080) > 0;
                    int rtp_pk_type = packet_type & 0x0000007f;

                    if (marker_set && (rtp_pk_type >= FIRST_RTCP_CONFLICT_PAYLOAD_TYPE)
                            && (rtp_pk_type <= LAST_RTCP_CONFLICT_PAYLOAD_TYPE)) {
                        
                        RtcpPacketInfo rcpi = new RtcpPacketInfo();
                        rcpi.flags = rpi.flags;
                        rcpi.packet_type = rpi.packet_type;
                        rcpi.length_seqNum = rpi.length_seqNum;
                        rcpi.sender_ssrc = ByteArrays.getInt(payload, offset);
                        offset = offset + 4;
                        if (packet_type == RtcpPacketInfo.sender_report_type) {
                            RtcpSenderReport senderReport = new RtcpSenderReport(rcpi);

                            senderReport.MSW_ts = ByteArrays.getInt(payload, offset);
                            // long ts_v=(long)senderReport.MSW_ts&0xffffffffL;
                            // System.out.println("msw_ts "+ts_v);
                            offset = offset + 4;
                            senderReport.LSW_ts = ByteArrays.getInt(payload, offset);
                            offset = offset + 4;
                            senderReport.RTP_ts = ByteArrays.getInt(payload, offset);
                            offset = offset + 4;
                            senderReport.sender_pk_count = ByteArrays.getInt(payload, offset);
                            offset = offset + 4;
                            senderReport.sender_oct_count = ByteArrays.getInt(payload, offset);
                            offset = offset + 4;
                            int report_count = senderReport.flags & 0x0000001f;
                            senderReport.source_ns = new Source_n[report_count];
                            // System.out.printf("%02x report count "+report_count+"
                            // \n",senderReport.flags);
                            for (int i = 0; i < senderReport.source_ns.length; i++) {
                                // Source_n source_n=new Source_n();
                                senderReport.source_ns[i] = new Source_n();
                                Source_n source_n = senderReport.source_ns[i];
                                // System.out.println("bbbbbbbbbbb "+source_n);
                                source_n.SR_id = ByteArrays.getInt(payload, offset);
                                offset = offset + 4;
                                source_n.fraction_loss = payload[offset] & 0x000000ff;
                                offset = offset + 1;
                                source_n.cumulative_num_loss = ByteArrays.getInt(payload, offset, 3);
                                offset = offset + 3;
                                source_n.seq_cycles_count = ByteArrays.getShort(payload, offset);
                                offset = offset + 2;
                                source_n.highest_seq_number_recv = ByteArrays.getShort(payload, offset);
                                offset = offset + 2;
                                source_n.inter_jitter = ByteArrays.getInt(payload, offset);
                                offset = offset + 4;
                                source_n.last_SR_ts = ByteArrays.getInt(payload, offset);
                                offset = offset + 4;
                                source_n.DLSR = ByteArrays.getInt(payload, offset);
                                offset = offset + 4;

                            }
                            rtpPackets.add(senderReport);
                        }else{
                            rtpPackets.add(rcpi);
                        }
                        packet_pos = packet_pos + (((int) rpi.length_seqNum & 0x0000ffff) + 1) * 4;
                    } else {
                        RtpDataPacketInfo rdpi = new RtpDataPacketInfo();
                        rdpi.flags = rpi.flags;
                        rdpi.packet_type = rpi.packet_type;
                        rdpi.length_seqNum = rpi.length_seqNum;
                        rdpi.timestamp = ByteArrays.getInt(payload, offset);
                        offset = offset + 4;
                        rdpi.sender_ssrc = ByteArrays.getInt(payload, offset);
                        offset = offset + 4;
                        rtpPackets.add(rdpi);
                        break;
                        // packet_pos=offset;
                        // System.out.println(packet_type+" aaaaaaaaaaaaaaa "+packet_pos+"
                        // "+payload.length+" "+rdpi.sender_ssrc);
                    }

                } catch (Exception e) {
                    StringBuilder sb = new StringBuilder(payload.length * 2);
                    for (byte b : payload) {
                        sb.append(String.format("%02x", b));
                    }
                    System.out.println("ver1 errrrrrrrrr " + sb.toString());
                    e.printStackTrace();
                    return new LinkedList<>();
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return rtpPackets;
    }
}
