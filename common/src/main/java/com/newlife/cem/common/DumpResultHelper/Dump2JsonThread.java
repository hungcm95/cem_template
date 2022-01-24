/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.newlife.cem.common.DumpResultHelper;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.File;
import java.io.FileWriter;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
// import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 *
 * @author hung_
 */
public class Dump2JsonThread extends Thread {

    private LinkedBlockingQueue<Object> handleQueue = new LinkedBlockingQueue<>();
    volatile boolean cancel = false;
    private Gson gson = new GsonBuilder().serializeSpecialFloatingPointValues().setPrettyPrinting().create();

    String out_dir;

    public Dump2JsonThread(String out_dir) {
        this.out_dir = out_dir;
    }

    @Override
    public void run() {
        Long lastDumpTs = null;
        while (!cancel) {
            try {
                Date curr_date = new Date();
                long curr_ts = curr_date.getTime();
                if (lastDumpTs == null) {
                    lastDumpTs = curr_ts;
                }
                if (curr_ts - lastDumpTs >= 30000) {
                    dumpAllQueueToJson(curr_ts);
                    lastDumpTs=curr_ts;
                }
                Thread.sleep(10000);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    private void dumpAllQueueToJson(long curr_ts) {
        try {
            Object o = null;
            List<Object> dumpList = new LinkedList<>();
            while ((o = handleQueue.poll()) != null) {
                dumpList.add(o);
            }
            if (dumpList.size() > 0) {
                File dir = new File(out_dir);
                dir.mkdirs();
                FileWriter writer = new FileWriter(out_dir + "\\"+out_dir+"_" + curr_ts + "_result.json");
                gson.toJson(dumpList, writer);
                writer.flush();
                writer.close();
                try {
                    // System.gc();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void addObject(Object obj) {
        handleQueue.add(obj);
    }

    public void flush() {
        Date curr_date = new Date();
        long curr_ts = curr_date.getTime();
        dumpAllQueueToJson(curr_ts);
    }

    public void cancel() {
        this.cancel = true;
    }
}
