package me.kroeker.alex.anchor.jserver.dao;

import java.util.Collection;

import me.kroeker.alex.anchor.jserver.api.exceptions.DataAccessException;
import me.kroeker.alex.anchor.jserver.model.Model;

/**
 * @author ak902764
 */
public interface ModelDAO {

    Model getModel(String connectionName, String modelId) throws DataAccessException;

    Collection<Model> getModels(String connectionName) throws DataAccessException;

}
