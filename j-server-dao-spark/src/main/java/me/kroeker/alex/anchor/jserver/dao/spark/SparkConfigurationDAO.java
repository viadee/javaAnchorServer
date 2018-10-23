package me.kroeker.alex.anchor.jserver.dao.spark;

import java.util.Collection;

import me.kroeker.alex.anchor.jserver.dao.ConfigurationDAO;
import me.kroeker.alex.anchor.jserver.dao.exceptions.DataAccessException;
import me.kroeker.alex.anchor.jserver.model.DataFrame;
import me.kroeker.alex.anchor.jserver.model.Model;

/**
 * @author ak902764
 */
public class SparkConfigurationDAO implements ConfigurationDAO {
    @Override
    public boolean tryConnect(String h2oServer) {
        return false;
    }

    @Override
    public Collection<String> getConnectionNames() {
        return null;
    }

    @Override
    public Collection<Model> getModels(String connectionName) throws DataAccessException {
        return null;
    }

    @Override
    public Collection<DataFrame> getFrames(String connectionName) throws DataAccessException {
        return null;
    }
}
