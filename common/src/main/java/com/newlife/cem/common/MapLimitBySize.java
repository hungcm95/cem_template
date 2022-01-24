/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.newlife.cem.common;

import java.util.LinkedHashMap;
import java.util.Map.Entry;

/**
 *
 * @author hung_
 */
@SuppressWarnings("serial")
public class MapLimitBySize<K, V> extends LinkedHashMap<K, V> {

    /**
     *
     */
    int wsize;

    public MapLimitBySize(final int wsize) {
        this.wsize = wsize;
    }

    @Override
    protected boolean removeEldestEntry(final Entry<K, V> eldest) {
        if (this.size() >= wsize) {
            // System.out.println(" Window overflow ");
            return true;
        }
        return super.removeEldestEntry(eldest); //To change body of generated methods, choose Tools | Templates.
    }

}
