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
public class Source_n {
    public int SR_id;
    public int fraction_loss;
    public int cumulative_num_loss;
    public short seq_cycles_count;
    public short highest_seq_number_recv;
    public int inter_jitter;
    public int last_SR_ts;
    public int DLSR;
}
