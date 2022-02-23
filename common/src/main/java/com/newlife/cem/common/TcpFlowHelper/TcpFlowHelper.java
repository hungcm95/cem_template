package com.newlife.cem.common.TcpFlowHelper;

import com.newlife.cem.common.Constant;
import com.newlife.cem.common.PcapHelper.TcpPacketInfo;

import org.pcap4j.packet.Packet;
import org.pcap4j.packet.TcpPacket;

public class TcpFlowHelper {
    final double init_rtt_thrd;

    public TcpFlowHelper(double init_rtt_thrd) {
        this.init_rtt_thrd = init_rtt_thrd;
    }

    long expected_seq_down = -1;
    double largest_seq_pk_ts_down = -1;
    long expected_seq_up = -1;
    double largest_seq_pk_ts_up = -1;

    public int getTcpPacketTransmitInfo(TcpPacketInfo packetInfo) {
        try {
            //need to impliment fast-retran
            // Long tsL = (long) Math.floor(packetInfo.timestamp);

            TcpPacket.TcpHeader tcpHeader = packetInfo.tcpPacket.getHeader();
            Packet tcpPayload = packetInfo.tcpPacket.getPayload();
            long seq_num = tcpHeader.getSequenceNumberAsLong();
            int payload_len = (tcpPayload != null) ? tcpPayload.length() : 0;
            boolean is_retran = false;
            boolean is_out_of_order = false;
            

            if (packetInfo.direction == 0) { // downlink
                long next_seq = seq_num + payload_len;
                if (payload_len > 1 || tcpHeader.getSyn() || tcpHeader.getFin()) {
                    if (expected_seq_down == -1) {
                        expected_seq_down = seq_num + payload_len;
                        largest_seq_pk_ts_down = packetInfo.timestamp;
                    } else {
                        if (seq_num > 0 || (tcpHeader.getSyn() || tcpHeader.getFin())) {
                            if (expected_seq_down > seq_num && expected_seq_down - seq_num < Integer.MAX_VALUE) {
                                is_retran = true;
                                if (next_seq != expected_seq_down
                                        && packetInfo.timestamp - largest_seq_pk_ts_down < init_rtt_thrd) {
                                    is_out_of_order = true;
                                }

                            }
                        }
                    }
                    // last_seq_down = seq_num;
                }
                if(seq_num + payload_len>expected_seq_down){
                    expected_seq_down = seq_num + payload_len;
                    largest_seq_pk_ts_down = packetInfo.timestamp;
                }
            }

            else if (packetInfo.direction == 1) { // downlink
                long next_seq = seq_num + payload_len;
                if (payload_len > 1 || tcpHeader.getSyn() || tcpHeader.getFin()) {
                    if (expected_seq_up == -1) {
                        expected_seq_up = seq_num + payload_len;
                        largest_seq_pk_ts_up = packetInfo.timestamp;
                    } else {
                        if (seq_num > 0 || (tcpHeader.getSyn() || tcpHeader.getFin())) {
                            if (expected_seq_up > seq_num && expected_seq_up - seq_num < Integer.MAX_VALUE) {
                                is_retran = true;
                                if (next_seq != expected_seq_up
                                        && packetInfo.timestamp - largest_seq_pk_ts_up < init_rtt_thrd) {
                                    is_out_of_order = true;
                                }

                            }
                        }
                    }
                    // last_seq_down = seq_num;
                }
                if(seq_num + payload_len>expected_seq_up){
                    expected_seq_up = seq_num + payload_len;
                    largest_seq_pk_ts_up = packetInfo.timestamp;
                }
            }



            if(is_out_of_order){
                return Constant.TcpTransmitType.OUTOFORDER;
            }else if(is_retran){
                return Constant.TcpTransmitType.RETRANSMISSION;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return Constant.TcpTransmitType.OK;
    }

}
