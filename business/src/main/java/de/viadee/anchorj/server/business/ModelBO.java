package de.viadee.anchorj.server.business;

import de.viadee.anchorj.server.api.exceptions.DataAccessException;
import de.viadee.anchorj.server.dao.ModelDAO;
import de.viadee.anchorj.server.model.Model;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collection;

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
