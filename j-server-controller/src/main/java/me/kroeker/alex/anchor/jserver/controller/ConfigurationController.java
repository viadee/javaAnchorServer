package me.kroeker.alex.anchor.jserver.controller;

import me.kroeker.alex.anchor.jserver.dao.ConfigurationDAO;
import me.kroeker.alex.anchor.jserver.dao.exceptions.DataAccessException;
import me.kroeker.alex.anchor.jserver.model.DataFrame;
import me.kroeker.alex.anchor.jserver.model.Model;
import me.kroeker.alex.anchor.jserver.model.TryConnectResponse;
import me.kroeker.alex.anchor.jserver.api.ConfigurationApi;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.ws.rs.core.MediaType;
import java.util.Collection;

/**
 * @author ak902764
 */
@RestController
public class ConfigurationController implements ConfigurationApi {

    private static final Logger LOG = LoggerFactory.getLogger(ConfigurationController.class);

    @Autowired
    private ConfigurationDAO configuration;

    @Override
    @RequestMapping(path = "/", method = RequestMethod.GET, headers = "Accept=application/json",
            produces = MediaType.APPLICATION_JSON)
    public String getVersion() {
        // TODO get version implementieren
        return "hello";
    }

    @Override
    @RequestMapping(path = "/{connectionName}/try_connect", method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON)
    public  TryConnectResponse tryConnect(@PathVariable String connectionName) {
        try {
            boolean canConnect = this.configuration.tryConnect(connectionName);
            return new TryConnectResponse(canConnect);
        } catch (DataAccessException dae) {
            LOG.error(dae.getMessage(), dae);
            // TODO add exception handling
            return null;
        }
    }

    @Override
    @RequestMapping(path = "/{connectionName}/models", method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON)
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
    @RequestMapping(path = "/{connectionName}/frames", method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON)
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
