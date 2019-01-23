package de.viadee.anchorj.server.dao.h2o;

import java.io.IOException;
import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import de.viadee.anchorj.server.configuration.AppConfiguration;
import de.viadee.anchorj.server.dao.ClusterDAO;
import de.viadee.anchorj.server.h2o.util.H2oConnector;
import water.bindings.pojos.AboutV3;

/**
 */
@Component
public class H2oClusterDAO implements ClusterDAO, H2oConnector {

    private AppConfiguration configuration;

    public H2oClusterDAO(@Autowired AppConfiguration configuration) {
        this.configuration = configuration;
    }

    @Override
    public boolean tryConnect(String connectionName) {
        try {
            AboutV3 about = this.createH2o(this.configuration, connectionName).about();
            return true;
        } catch (IOException | IllegalArgumentException ioe) {
            return false;
        }
    }

    @Override
    public Collection<String> getConnectionNames() {
        return this.configuration.getH2oConnectionNames();
    }

}
