package me.kroeker.alex.anchor.jserver.business;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import me.kroeker.alex.anchor.jserver.api.exceptions.DataAccessException;
import me.kroeker.alex.anchor.jserver.dao.ModelDAO;
import me.kroeker.alex.anchor.jserver.model.Model;

/**
 */
@Component
public class ModelBO {

    private ModelDAO modelDAO;

    public ModelBO(@Autowired ModelDAO modelDAO) {
        this.modelDAO = modelDAO;
    }

    public Model getModel(String connectionName, String modelId) throws DataAccessException {
        return this.modelDAO.getModel(connectionName, modelId);
    }

    public Collection<Model> getModels(String connectionName) throws DataAccessException {
        return this.modelDAO.getModels(connectionName);
    }
}
