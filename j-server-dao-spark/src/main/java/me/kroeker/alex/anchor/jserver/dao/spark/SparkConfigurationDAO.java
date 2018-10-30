package me.kroeker.alex.anchor.jserver.dao.spark;

import java.util.Collection;

import me.kroeker.alex.anchor.jserver.api.exceptions.DataAccessException;
import me.kroeker.alex.anchor.jserver.dao.ConfigurationDAO;
import me.kroeker.alex.anchor.jserver.model.DataFrame;
import me.kroeker.alex.anchor.jserver.model.Model;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

/**
 * @author ak902764
 */
public class SparkConfigurationDAO implements ConfigurationDAO {
    @Override
    public boolean tryConnect(String h2oServer) {
        throw new NotImplementedException();
    }

    @Override
    public Collection<String> getConnectionNames() {
        throw new NotImplementedException();
    }

    @Override
    public Model getModel(String connectionName, String modelId) throws DataAccessException {
        throw new NotImplementedException();
    }

    @Override
    public Collection<Model> getModels(String connectionName) throws DataAccessException {
        throw new NotImplementedException();
    }

    @Override
    public Collection<DataFrame> getFrames(String connectionName) throws DataAccessException {
        throw new NotImplementedException();
    }
}
