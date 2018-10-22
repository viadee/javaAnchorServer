package me.kroeker.alex.anchor.jserver.dao.h2o;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import me.kroeker.alex.anchor.jserver.dao.ConfigurationDAO;
import water.bindings.H2oApi;
import water.bindings.pojos.AboutV3;

/**
 * @author ak902764
 */
public class ConfigurationH2o implements ConfigurationDAO {

    private static final Logger LOG = LoggerFactory.getLogger(ConfigurationH2o.class);

    @Override
    public boolean tryConnect(String h2oServer) {
        try {
            AboutV3 about = new H2oApi(h2oServer).about();
            return true;
        } catch (IOException ioe) {
            LOG.error("Failed to connect to h2o server: " + h2oServer, ioe);
        }
        return false;
    }
}
