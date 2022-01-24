/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.newlife.cem.common.PcapHelper;

import java.net.Inet4Address;
import org.pcap4j.util.MacAddress;


/**
 *
 * @author hung_
 */
public class PacketFlowInfo {
    public long flow_idx;
    public long ts_idx;
    public int flow_direct=-1;
    public MacAddress macSrc;
    public MacAddress macDst;
    
    public Inet4Address ipSrc;
    public Inet4Address ipDst;
    public int l4Proto = -1;
    public int srcPort;
    public int dstPort;

    @Override
    public int hashCode() {
        return ipSrc.hashCode() + ipDst.hashCode() + l4Proto + srcPort + dstPort;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (this.getClass() != obj.getClass()) {
            return false;
        }
        PacketFlowInfo other = (PacketFlowInfo) obj;
        if (this.l4Proto == other.l4Proto) {
            if ((this.ipSrc.equals(other.ipSrc) && this.ipDst.equals(other.ipDst))
                    || (this.ipSrc.equals(other.ipDst) && this.ipDst.equals(other.ipSrc))) {
                if ((this.srcPort == other.srcPort && this.dstPort == other.dstPort)
                        || (this.srcPort == other.dstPort && this.dstPort == other.srcPort)) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public String toString() {
        String l4_proto_str=this.l4Proto+"";
        if(this.l4Proto==17){
            l4_proto_str="UDP";
        }else if(this.l4Proto==06){
            l4_proto_str="TCP";
        }
        if(this.flow_direct==0){
            return l4_proto_str +" "+ this.ipDst + ":" + this.dstPort + " <-> " + this.ipSrc + ":" + this.srcPort;
        }
        return l4_proto_str +" "+ this.ipSrc + ":" + this.srcPort + " <-> " + this.ipDst + ":" + this.dstPort;
    }
    
    
}
