package me.kroeker.alex.anchor.jserver.api;

import java.util.Collection;

import me.kroeker.alex.anchor.jserver.model.TryConnectResponse;

/**
 */
public interface ClusterApi {

    TryConnectResponse tryConnect(String connectionName);

    Collection<String> getConnectionNames();

}
