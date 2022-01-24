package com.newlife.template;
import java.util.concurrent.ConcurrentHashMap;
import com.newlife.cem.common.PcapHelper.InterfacePcapHandle;
import org.pcap4j.core.PcapHandle;
/**
 * Hello world!
 *
 */
public class App 
{
    public static final int NUMBER_THREAD = 40;
    public static volatile boolean isReadPcapFinish = false;
    public static ConcurrentHashMap<Integer, PacketHandleThread> packetHandleWorkerList = new ConcurrentHashMap<>();
    // static byte[] dst_mac = new byte[6];
    
    // public static String cap_filter = "(pppoed || pppoes) and ip and (tcp or udp)";
    public static String cap_filter = "ip and (tcp or udp)";
    public static byte[] dst_mac = new byte[6];
    public static void main(String[] args) {
        try {

            initWorkerList();

            try {
                // String macAddress = args[2];
                // String macAddress = "7c:8b:ca:ba:b8:54";
                // String macAddress = "5c:45:27:7c:a3:07";
                String macAddress = "48:ad:08:39:c9:00";
                String[] macAddressParts = macAddress.split(":");
                for (int i = 0; i < 6; i++) {
                    Integer hex = Integer.parseInt(macAddressParts[i], 16);
                    dst_mac[i] = hex.byteValue();
                }
            } catch (Exception e) {
                System.out.println("parse mac address error");
                e.printStackTrace();
                System.exit(0);
            }

            
            InterfacePcapHandle iph = new InterfacePcapHandle("-i",
            "\\Device\\NPF_{2D2AB05D-A87F-4AE4-8A7C-A57E628F2116}");

            // InterfacePcapHandle iph = new InterfacePcapHandle("-r",
            // "C:\\Users\\hung_\\Downloads\\1_stall.pcapng");

            // InterfacePcapHandle iph = new InterfacePcapHandle(args[0], args[1],
                    // cap_filter);
            PcapHandle pcapHandle = iph.getPcapHandle();
            Runtime.getRuntime().addShutdownHook(new Thread() {
                @Override
                public void run() {
                    try {
                        iph.printDropInfo();
                    } catch (Exception e) {
                        System.out.println("error khi in packets dropped ");
                    }

                }
            });

            MyPacketListener myPacketListener = new MyPacketListener(pcapHandle);
            pcapHandle.loop(-1, myPacketListener);
            Thread.sleep(4000);
            isReadPcapFinish = true;
            // wait all worker thread finish
            for (Thread workerThread : packetHandleWorkerList.values()) {
                workerThread.join();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void initWorkerList() {
        for (int i = 0; i < NUMBER_THREAD; i++) {
            PacketHandleThread handleThread = new PacketHandleThread(i,dst_mac);
            handleThread.start();
            // System.out.println("put "+i);
            packetHandleWorkerList.put(i, handleThread);
        }
    }

    
}
