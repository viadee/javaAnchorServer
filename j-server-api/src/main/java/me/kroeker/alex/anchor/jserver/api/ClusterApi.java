package me.kroeker.alex.anchor.jserver.api;

import java.util.Collection;

import me.kroeker.alex.anchor.jserver.model.TryConnectResponse;

/**
 * @author ak902764
 */
public interface ClusterApi {

    TryConnectResponse tryConnect(String connectionName);

    Collection<String> getConnectionNames();

}
