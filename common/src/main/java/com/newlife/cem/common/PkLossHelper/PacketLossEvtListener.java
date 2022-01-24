/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.newlife.cem.common.PkLossHelper;

import java.util.Map;

/**
 *
 * @author hung_
 */
public interface PacketLossEvtListener {

    public void lossPkEvt(Map.Entry<Long, Double> lossPkInfo);
    
}
