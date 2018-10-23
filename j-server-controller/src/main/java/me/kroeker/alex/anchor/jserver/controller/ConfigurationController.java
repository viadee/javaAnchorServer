package me.kroeker.alex.anchor.jserver.controller;

import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import me.kroeker.alex.anchor.jserver.dao.ConfigurationDAO;
import me.kroeker.alex.anchor.jserver.dao.exceptions.DataAccessException;
import me.kroeker.alex.anchor.jserver.model.DataFrame;
import me.kroeker.alex.anchor.jserver.model.Model;
import me.kroeker.alex.anchor.jserver.service.ConfigurationService;

/**
 * @author ak902764
 */
@RestController
public class ConfigurationController implements ConfigurationService {

    private static final Logger LOG = LoggerFactory.getLogger(ConfigurationController.class);

    @Autowired
    private ConfigurationDAO configuration;

    @Override
    @RequestMapping(path = "/", method = RequestMethod.GET, headers = "Accept=application/json", produces = {
            "application/json" })
    public String getVersion() {
        // TODO get version implementieren
        return "hello";
    }

    @Override
    @RequestMapping(path = "/{connectionName}/try_connect", method = RequestMethod.GET, produces = { "application/json" })
    public Boolean tryConnect(@PathVariable String connectionName) {
        try {
            return this.configuration.tryConnect(connectionName);
        } catch (DataAccessException dae) {
            LOG.error(dae.getMessage(), dae);
            // TODO add exception handling
            return null;
        }
    }

    @Override
    @RequestMapping(path = "/{connectionName}/models", method = RequestMethod.GET, produces = { "application/json" })
    public Collection<Model> getModels(@PathVariable String connectionName) {
        try {
            return this.configuration.getModels(connectionName);
        } catch (DataAccessException dae) {
            LOG.error(dae.getMessage(), dae);
            // TODO add exception handling
            return null;
        }
    }

    @Override
    @RequestMapping(path = "/{connectionName}/frames", method = RequestMethod.GET, produces = { "application/json" })
    public Collection<DataFrame> getFrames(@PathVariable String connectionName) {
        try {
            return this.configuration.getFrames(connectionName);
        } catch (DataAccessException dae) {
            LOG.error(dae.getMessage(), dae);
            // TODO add exception handling
            return null;
        }
    }

}
