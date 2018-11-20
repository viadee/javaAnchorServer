package me.kroeker.alex.anchor.jserver.api;

import me.kroeker.alex.anchor.jserver.model.TryConnectResponse;

import java.util.Collection;

/**
 */
public interface ClusterApi {

    TryConnectResponse tryConnect(String connectionName);

    Collection<String> getConnectionNames();

}
