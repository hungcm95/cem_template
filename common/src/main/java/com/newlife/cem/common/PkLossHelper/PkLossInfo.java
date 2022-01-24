/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.newlife.cem.common.PkLossHelper;

/**
 *
 * @author hung_
 */
public class PkLossInfo {

    public PkLossInfo(int num_pk_loss, int total_pk) {
        this.num_pk_loss = num_pk_loss;
        this.total_pk = total_pk;
    }
    public PkLossInfo() {
    }

    public PkLossInfo(int num_pk_loss, int total_pk,Integer p2p_info) {
        this.num_pk_loss = num_pk_loss;
        this.total_pk = total_pk;
        this.p2p_info=p2p_info;
    }
    
    public int num_pk_loss=0;
    public int total_pk=0;
    public Integer p2p_info=0;
}
