package de.viadee.anchorj.server.api;

import de.viadee.anchorj.server.model.ConnectionNameListResponse;
import de.viadee.anchorj.server.model.TryConnectResponse;

import java.util.Collection;

/**
 */
public interface ClusterApi {

    TryConnectResponse tryConnect(String connectionName);

    ConnectionNameListResponse getConnectionNames();

}
