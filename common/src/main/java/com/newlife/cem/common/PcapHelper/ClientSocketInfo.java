package com.newlife.cem.common.PcapHelper;

public class ClientSocketInfo {
    int ip;
    int port;

    public ClientSocketInfo(int ip, int port) {
        this.ip = ip;
        this.port = port;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this)
            return true;
        if (!(o instanceof ClientSocketInfo)) {
            return false;
        }
        ClientSocketInfo clientSocketInfo = (ClientSocketInfo) o;
        return ip == clientSocketInfo.ip && port == clientSocketInfo.port;
    }

    @Override
    public int hashCode() {
       return ip+port;
    }


    @Override
    public String toString() {
        return "{" +
            " ip='" + this.ip + "'" +
            ", port='" + this.port + "'" +
            "}";
    }

}
