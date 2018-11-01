package me.kroeker.alex.anchor.jserver.dao;

import java.util.Collection;

import me.kroeker.alex.anchor.jserver.api.exceptions.DataAccessException;

/**
 * @author ak902764
 */
public interface ClusterDAO {

    boolean tryConnect(String connectionName) throws DataAccessException;

    Collection<String> getConnectionNames();

}
