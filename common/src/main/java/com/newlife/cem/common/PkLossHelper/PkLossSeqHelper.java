/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.newlife.cem.common.PkLossHelper;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;

import com.newlife.cem.common.MapLimitBySize;

/**
 *
 * @author hung_
 */
public class PkLossSeqHelper {

    int window_size;
    LinkedHashMap<Integer, Integer> PkWindow;

    public PkLossSeqHelper(int window_size) {
        this.window_size = window_size;
        PkWindow = new MapLimitBySize<>(window_size + 2);
    }

    public PkLossSeqHelper() {
        this.window_size = 10;
        PkWindow = new MapLimitBySize<>(window_size + 2);
    }
    int expected_msg_seq = 0;

    public void PkLossSeqHandle(int msg_seq, List<Integer> resultsByTime) {
        PkWindow.put(msg_seq, msg_seq);

        if (PkWindow.size() > (window_size)) {
            int min_seq = Collections.min(PkWindow.values());
//            System.out.println("Min_seq " + min_seq);
            if (msg_seq > 65000 || msg_seq < 500) {
                int max_seq = Collections.max(PkWindow.values());
                if (max_seq - min_seq > 60000) {
                    //reset var
//                                        System.out.println("reset window");
                    expected_msg_seq = min_seq;
                    PkWindow.remove(max_seq);
//                                        downPkWindow = new MapLimitBySize(window_size);
                    return;
                }
            }
            if (expected_msg_seq > 0) {
                int num_loss = 0;
                boolean alowAdd=true;
                if(min_seq==26281){
                    // System.out.println(min_seq+" CCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCC"+expected_msg_seq);
                }
                if (min_seq >= expected_msg_seq) {
                    num_loss = Math.abs(min_seq - expected_msg_seq);
                    expected_msg_seq = min_seq + 1;
//                    System.out.println("packet loss detect " + min_seq + " " + expected_msg_seq_down);
                } else if (min_seq<expected_msg_seq) {
                    // expected_msg_seq = expected_msg_seq;
                    // System.out.println("AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
                    alowAdd=false;
                }
                if(alowAdd){
                    resultsByTime.add(num_loss);
                }
//                    System.out.println("Min_seq " + min_seq+" Expected_seq: "+expected_msg_seq+ " numPkLoss "+num_loss);
            } else {
                expected_msg_seq = min_seq + 1;
            }

            //remove
            PkWindow.remove(min_seq);
        } else {
//            System.out.println("xxxxxxxxxx " + PkWindow.size());
        }
    }
}
