package de.viadee.anchorj.server.api;

import de.viadee.anchorj.server.model.ConnectionNameListResponse;
import de.viadee.anchorj.server.model.TryConnectResponse;

/**
 */
public interface ClusterApi {

    String getVersion();

    TryConnectResponse tryConnect(String connectionName);

    ConnectionNameListResponse getConnectionNames();

}
