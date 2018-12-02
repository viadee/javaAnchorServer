package de.viadee.anchorj.server.model;

public class TryConnectResponse {

    private boolean can_connect;

    public TryConnectResponse(boolean can_connect) {
        this.can_connect = can_connect;
    }

    public boolean isCan_connect() {
        return can_connect;
    }

    public void setCan_connect(boolean can_connect) {
        this.can_connect = can_connect;
    }

}
