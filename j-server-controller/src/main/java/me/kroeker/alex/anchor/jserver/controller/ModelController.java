package me.kroeker.alex.anchor.jserver.controller;

import java.util.Collection;

import javax.ws.rs.core.MediaType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import me.kroeker.alex.anchor.jserver.api.ModelApi;
import me.kroeker.alex.anchor.jserver.api.exceptions.DataAccessException;
import me.kroeker.alex.anchor.jserver.business.ModelBO;
import me.kroeker.alex.anchor.jserver.model.Model;

/**
 */
@RestController
public class ModelController implements ModelApi {

    private static final Logger LOG = LoggerFactory.getLogger(ModelController.class);

    private ModelBO modelBO;

    public ModelController(@Autowired ModelBO modelBO) {
        this.modelBO = modelBO;
    }

    @Override
    @RequestMapping(
            path = "/{connectionName}/models",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON
    )
    public Collection<Model> getModels(@PathVariable String connectionName) {
        try {
            return this.modelBO.getModels(connectionName);
        } catch (DataAccessException dae) {
            LOG.error(dae.getMessage(), dae);
            // TODO add exception handling
            return null;
        }
    }

    @Override
    @RequestMapping(
            path = "/{connectionName}/models/{modelId}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON
    )
    public Model getModel(@PathVariable String connectionName, @PathVariable String modelId) {
        try {
            return this.modelBO.getModel(connectionName, modelId);
        } catch (DataAccessException dae) {
            LOG.error(dae.getMessage(), dae);
            // TODO add exception handling
            return null;
        }
    }

}
