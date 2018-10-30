package me.kroeker.alex.anchor.jserver.dao;

import me.kroeker.alex.anchor.jserver.api.exceptions.DataAccessException;
import me.kroeker.alex.anchor.jserver.model.DataFrame;
import me.kroeker.alex.anchor.jserver.model.Model;

import java.util.Collection;

/**
 * @author ak902764
 */
public interface ConfigurationDAO {
    boolean tryConnect(String connectionName) throws DataAccessException;

    Collection<String> getConnectionNames();

    Model getModel(String connectionName, String modelId) throws DataAccessException;

    Collection<Model> getModels(String connectionName) throws DataAccessException;

    Collection<DataFrame> getFrames(String connectionName) throws DataAccessException;
}
