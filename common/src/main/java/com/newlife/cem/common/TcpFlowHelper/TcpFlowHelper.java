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

    Long lastGetTcpRestranTs = null;
    long expected_seq_up = -1;
    long last_seq_up = -1;
    double largest_seq_pk_ts_up = -1;
    long largest_seq_pk_up=-1;
    // long numberRestranmissUp = 0;
    long expected_seq_down = -1;
    long last_seq_down = -1;
    double largest_seq_pk_ts_down = -1;
    long largest_seq_pk_down=-1;

    long last_ack_seq_up = -1;
    long last_ack_seq_down = -1;
    // long numberRestranmissDown = 0;
    public int getTcpPacketTransmitInfo(TcpPacketInfo packetInfo) {
        try {
            Long tsL = (long) Math.floor(packetInfo.timestamp);
            if (lastGetTcpRestranTs == null) {
                lastGetTcpRestranTs = tsL;
            }
            TcpPacket.TcpHeader tcpHeader = packetInfo.tcpPacket.getHeader();
            
            Packet tcpPayload = packetInfo.tcpPacket.getPayload();
            long seq_num = tcpHeader.getSequenceNumberAsLong();

            int payload_len = (tcpPayload != null) ? tcpPayload.length() : 0;
            boolean is_retran = false;
            if (packetInfo.direction == 0) { // downlink
                
                if (tcpHeader.getAck()) {
                    last_ack_seq_down = tcpHeader.getAcknowledgmentNumberAsLong();
                }
                if (payload_len > 1 || tcpHeader.getSyn() || tcpHeader.getFin()) {
                    if (expected_seq_down == -1) {
                        expected_seq_down = seq_num + payload_len;
                        largest_seq_pk_ts_down = packetInfo.timestamp;
                        largest_seq_pk_down=seq_num;
                    } else {
                        if (seq_num > 0 || (tcpHeader.getSyn() || tcpHeader.getFin())) {
                            if (expected_seq_down > seq_num && expected_seq_down-seq_num<Integer.MAX_VALUE) {
                                long next_expected_seq = seq_num + payload_len;
                                boolean is_out_of_order = false;
                                if (expected_seq_down >= next_expected_seq && last_seq_down != seq_num) {
                                    if (largest_seq_pk_ts_down != -1
                                            && (packetInfo.timestamp - largest_seq_pk_ts_down < init_rtt_thrd)) {
                                        is_out_of_order = true;
                                    }
                                    if (last_ack_seq_up > 0 && seq_num <= last_ack_seq_up) { // if seq < lastest ack
                                                                                             // sequence up
                                        is_out_of_order = false;
                                    }

                                    // if(tcpHeader.getDstPort().valueAsInt()==51111){
                                    // System.out.println(expected_seq_down+" AAAAAAAAAAA "+next_expected_seq);
                                    // }
                                }
                                if (is_out_of_order) {
                                    return Constant.TcpTransmitType.OUTOFORDER;
                                } else {
                                    // System.out.println("retran "+packetInfo.timestamp);
                                    is_retran = true;
                                }

                            } else {
                                if (expected_seq_down == seq_num && last_seq_down == seq_num
                                        && (tcpHeader.getSyn() || tcpHeader.getFin())) {
                                    is_retran = true;
                                }
                                expected_seq_down = seq_num + payload_len;
                                largest_seq_pk_ts_down = packetInfo.timestamp;
                                largest_seq_pk_down=seq_num;
                            }

                            if (is_retran) {
                                return Constant.TcpTransmitType.RETRANSMISSION;
                            } else {

                            }
                        }
                    }
                    last_seq_down = seq_num;
                }

            }

            if (packetInfo.direction == 1) {// uplink
                if (tcpHeader.getAck()) {
                    last_ack_seq_up = tcpHeader.getAcknowledgmentNumberAsLong();
                }
                if (payload_len > 1 || tcpHeader.getSyn() || tcpHeader.getFin()) {

                    if (expected_seq_up == -1) {
                        expected_seq_up = seq_num + payload_len;
                        largest_seq_pk_ts_up = packetInfo.timestamp;
                        largest_seq_pk_up=seq_num;
                    } else {
                        if (seq_num > 0 || tcpHeader.getSyn() || tcpHeader.getFin()) {
                            if (expected_seq_up > seq_num && expected_seq_up-seq_num<Integer.MAX_VALUE) {
                                long next_expected_seq = seq_num + payload_len;
                                boolean is_out_of_order = false;
                                if (expected_seq_up >= next_expected_seq && last_seq_up != seq_num) {
                                    if (largest_seq_pk_ts_up != -1
                                            && (packetInfo.timestamp - largest_seq_pk_ts_up < init_rtt_thrd)) {
                                        is_out_of_order = true;
                                        // if (tcpHeader.getSrcPort().valueAsInt() == 54599) {
                                        // System.out.println(last_ack_seq_up + " AAAAAAAAAAA " + seq_num);
                                        // }
                                    }
                                }
                                if (last_ack_seq_down > 0 && seq_num <= last_ack_seq_down) {
                                    is_out_of_order = false;
                                }
                                // if (tcpHeader.getSrcPort().valueAsInt() == 54599) {
                                // System.out.println(last_ack_seq_up + " AAAAAAAAAAA " + seq_num);
                                // }
                                if (is_out_of_order) {
                                    return Constant.TcpTransmitType.OUTOFORDER;
                                } else {
                                    is_retran = true;
                                    
                                }

                            }

                            else {
                                if (expected_seq_up == seq_num && last_seq_up == seq_num
                                        && (tcpHeader.getSyn() || tcpHeader.getFin())) {

                                    is_retran = true;
                                }

                                expected_seq_up = seq_num + payload_len;
                                largest_seq_pk_ts_up = packetInfo.timestamp;
                                largest_seq_pk_up=seq_num;
                            }

                            if (is_retran) {
                                return Constant.TcpTransmitType.RETRANSMISSION;

                            } else {

                            }
                        }
                    }
                }

                last_seq_up = seq_num;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Constant.TcpTransmitType.OK;
    }

    public double getLargestSeqPkTs(int direction){
        if(direction==1){
            return largest_seq_pk_ts_up;
        }
        if(direction==0){
            return largest_seq_pk_ts_down;
        }
        return -1;
    }
    public long getLargestSeqPk(int direction){
        if(direction==1){
            return largest_seq_pk_up;
        }
        if(direction==0){
            return largest_seq_pk_down;
        }
        return -1;
    }
}
