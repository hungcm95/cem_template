/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.newlife.cem.common.PkLossHelper;

import java.util.LinkedHashMap;
import java.util.Map.Entry;

/**
 *
 * @author hung_
 */
@SuppressWarnings("serial")
public class SeqMatchingMap<K> extends LinkedHashMap<K, Double> {

    double Pk_Loss_Ts_Thrd;
    private Double newest_value = null;
    public int packet_loss_count=0;
    public int total_packet_count=0;


    public SeqMatchingMap() {
        Pk_Loss_Ts_Thrd=1.5;
    }


    public SeqMatchingMap(double Pk_Loss_Ts_Thrd) {
        this.Pk_Loss_Ts_Thrd = Pk_Loss_Ts_Thrd;
    }

    @Override
    protected boolean removeEldestEntry(Entry<K, Double> eldest) {
        if(newest_value !=null){
            if(newest_value-eldest.getValue()>Pk_Loss_Ts_Thrd){
                packet_loss_count++;
                return true;
            }
        }
        return super.removeEldestEntry(eldest); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Double put(K key, Double value) {
        newest_value=value;
        total_packet_count++;
        return super.put(key, value); //To change body of generated methods, choose Tools | Templates.
    }
    
    
}
