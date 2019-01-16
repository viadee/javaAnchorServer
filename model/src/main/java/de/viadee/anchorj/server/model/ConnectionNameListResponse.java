package de.viadee.anchorj.server.model;

import java.util.Collection;
import java.util.Objects;

/**
 *
 */
public class ConnectionNameListResponse {
    Collection<String> connectionNames;

    public ConnectionNameListResponse(Collection<String> connectionNames) {
        this.connectionNames = connectionNames;
    }

    public Collection<String> getConnectionNames() {
        return connectionNames;
    }

    public void setConnectionNames(Collection<String> connectionNames) {
        this.connectionNames = connectionNames;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ConnectionNameListResponse that = (ConnectionNameListResponse) o;
        return Objects.equals(connectionNames, that.connectionNames);
    }

    @Override
    public int hashCode() {
        return Objects.hash(connectionNames);
    }

    @Override
    public String toString() {
        return "ConnectionNameListResponse{" +
                "connectionNames=" + connectionNames +
                '}';
    }

}
