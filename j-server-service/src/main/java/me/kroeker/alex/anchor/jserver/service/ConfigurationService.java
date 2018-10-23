package me.kroeker.alex.anchor.jserver.service;

import java.util.Collection;

import me.kroeker.alex.anchor.jserver.model.DataFrame;
import me.kroeker.alex.anchor.jserver.model.Model;

/**
 * @author ak902764
 */
public interface ConfigurationService {

    String getVersion();

    Boolean tryConnect(String connectionName);

    Collection<Model> getModels(String connectionName);

    Collection<DataFrame> getFrames(String connectionName);

}
