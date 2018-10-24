package me.kroeker.alex.anchor.jserver.service;

import me.kroeker.alex.anchor.jserver.model.DataFrame;
import me.kroeker.alex.anchor.jserver.model.Model;
import me.kroeker.alex.anchor.jserver.model.TryConnectResponse;

import java.util.Collection;

/**
 * @author ak902764
 */
public interface ConfigurationService {

    String getVersion();

    TryConnectResponse tryConnect(String connectionName);

    Collection<Model> getModels(String connectionName);

    Collection<DataFrame> getFrames(String connectionName);

}
