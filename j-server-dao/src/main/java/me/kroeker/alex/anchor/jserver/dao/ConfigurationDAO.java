package me.kroeker.alex.anchor.jserver.dao;

import java.util.Collection;

import me.kroeker.alex.anchor.jserver.dao.exceptions.DataAccessException;
import me.kroeker.alex.anchor.jserver.model.DataFrame;
import me.kroeker.alex.anchor.jserver.model.Model;

/**
 * @author ak902764
 */
public interface ConfigurationDAO {
    boolean tryConnect(String connectionName) throws DataAccessException;

    Collection<String> getConnectionNames();

    Collection<Model> getModels(String connectionName) throws DataAccessException;

    Collection<DataFrame> getFrames(String connectionName) throws DataAccessException;
}
