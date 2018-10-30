package me.kroeker.alex.anchor.jserver.business;

import me.kroeker.alex.anchor.jserver.api.exceptions.DataAccessException;
import me.kroeker.alex.anchor.jserver.dao.ConfigurationDAO;
import me.kroeker.alex.anchor.jserver.model.DataFrame;
import me.kroeker.alex.anchor.jserver.model.Model;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collection;

/**
 * @author ak902764
 */
@Component
public class ConfigurationBO {

    @Autowired
    private ConfigurationDAO configuration;

    public String getVersion() {
        // TODO get version implementieren
        return "hello";
    }

    public boolean tryConnect(String connectionName) throws DataAccessException {
        return this.configuration.tryConnect(connectionName);
    }

    public Model getModel(String connectionName, String modelId) throws DataAccessException {
        return this.configuration.getModel(connectionName, modelId);
    }

    public Collection<Model> getModels(String connectionName) throws DataAccessException {
        return this.configuration.getModels(connectionName);
    }

    public Collection<DataFrame> getFrames(String connectionName) throws DataAccessException {
        return this.configuration.getFrames(connectionName);
    }
}
