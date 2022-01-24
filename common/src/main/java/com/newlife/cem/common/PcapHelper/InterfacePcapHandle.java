/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.newlife.cem.common.PcapHelper;

import com.sun.jna.Platform;
import org.pcap4j.core.BpfProgram;
import org.pcap4j.core.NotOpenException;
import org.pcap4j.core.PcapHandle;
import org.pcap4j.core.PcapNativeException;
import org.pcap4j.core.PcapNetworkInterface;
import org.pcap4j.core.PcapStat;
import org.pcap4j.core.Pcaps;


/**
 *
 * @author hung_
 */
public class InterfacePcapHandle {

    String type;
    String interface_pcap;
    String capture_filter = null;
    private PcapHandle pcapHandle = null;

    public InterfacePcapHandle(String type, String interface_pcap) throws PcapNativeException, NotOpenException{
        this.type = type;
        this.interface_pcap = interface_pcap;
        openPcapInterface();
    }

    public InterfacePcapHandle(String type, String interface_pcap, String capture_filter) throws PcapNativeException, NotOpenException {
        this.type = type;
        this.interface_pcap = interface_pcap;
        this.capture_filter = capture_filter;
        openPcapInterface();
    }

    private void openPcapInterface() throws PcapNativeException, NotOpenException {
        
        if (type.equals("-r")) {
            System.out.println("path " + interface_pcap);
            pcapHandle = Pcaps.openOffline(interface_pcap);
        } else if (type.equals("-i")) {
            int snapshotLength = 65536; // in bytes   
            int readTimeout = 1000; // in milliseconds    
            int buffer_size = 1000 * 1024 * 1024;
            PcapNetworkInterface dev_inf = Pcaps.getDevByName(interface_pcap);
            System.out.println("dev_inf " + dev_inf);
            PcapHandle.Builder phb
                    = new PcapHandle.Builder(dev_inf.getName())
                            .snaplen(snapshotLength)
                            .promiscuousMode(PcapNetworkInterface.PromiscuousMode.PROMISCUOUS)
                            .timeoutMillis(readTimeout)
                            .bufferSize(buffer_size);
            pcapHandle = phb.build();
            if (capture_filter != null) {
                pcapHandle.setFilter(capture_filter, BpfProgram.BpfCompileMode.OPTIMIZE);
            }
        } else {
            System.out.println("Cannot find option -> exit");
            System.exit(-1);
        }
    }

    public PcapHandle getPcapHandle() {
//        if(pcapHandle==null){
//            System.out.println("AAAAAAAAAAAAAAAAAAAAA");
//        }
        return pcapHandle;
    }
    

    public void printDropInfo() {
        try {
            PcapStat stats = pcapHandle.getStats();
            System.out.println("Packets received: " + stats.getNumPacketsReceived());
            System.out.println("Packets dropped: " + stats.getNumPacketsDropped());
            System.out.println("Packets dropped by interface: " + stats.getNumPacketsDroppedByIf());
            // Supported by WinPcap only
            if (Platform.isWindows()) {
                System.out.println("Packets captured: " + stats.getNumPacketsCaptured());
            }
        } catch (Exception e) {
            System.out.println("error khi in packets dropped ");
        }
    }
}
