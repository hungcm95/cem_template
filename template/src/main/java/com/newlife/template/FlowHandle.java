package com.newlife.template;

import com.newlife.cem.common.PcapHelper.PacketFlowInfo;
import com.newlife.cem.common.PcapHelper.PacketInfo;

/**
 *
 * @author hung_
 */
public abstract class FlowHandle {
    public double last_add_pk_ts = -1;
    PacketFlowInfo flowInfo;

    public FlowHandle(PacketFlowInfo flowInfo) {
        this.flowInfo = flowInfo;
    }

    abstract void addPacket(PacketInfo packetInfo);

    abstract void flowEndEvt();
}
