package com.newlife.cem.common;

/**
 * Constant
 */
public class Constant {
    public static class ServiceId {
        public static final int ZALO_SERVICE = 20;
    }

    public static class StreamType {
        public static final int CLIENT_SERVER = 0;
        public static final int P2P = 1;
        public static final int BOTH = 2;
    }

    public static class ServiceKpi {
        public static final int DELAY = 1;
        public static final int PACKET_LOSS = 2;
        public static final int ABNORMAL_ENDING = 3;
        public static final int LOSS_CONNECTION = 4;
    }

    public static class NetworkArea {
        public static final int CLIENT_2_CAPTURE = 1;
        public static final int CAPTURE_2_SERVER = 2;
        public static final int CAPTURE_2_OTHER_CLIENT_OVER_SERVER = 3;
        public static final int CLIENT_2_SERVER = 4;
        public static final int CLIENT_2_OTHER_CLIENT_OVER_SERVER = 5;

        public static final int CAPTURE_2_OTHER_CLIENT_P2P = 6;
        public static final int CLIENT_2_OTHER_CLIENT_P2P = 7;
    }

    public static class NetworkDirection {
        public static final int DOWNLINK = 0;
        public static final int UPLINK = 1;
        public static final int BOTHLINK = 2;
    }

    public static class WarningType {
        public static final int WARNING_DELAY_TYPE = 1;
        public static final int WARNING_JITTER_TYPE = 2;
        public static final int WARNING_PKLOSS_TYPE = 3;
    }

    public static class TcpTransmitType{
        public static final int OK = 0;
        public static final int OUTOFORDER = 1;
        public static final int RETRANSMISSION = 2;
    }

}