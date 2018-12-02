package de.viadee.anchorj.server.dao.h2o;

import java.io.IOException;
import java.util.Collection;

import org.springframework.stereotype.Component;
import de.viadee.anchorj.server.dao.ClusterDAO;
import de.viadee.anchorj.server.h2o.util.H2oConnector;
import de.viadee.anchorj.server.h2o.util.H2oUtil;
import water.bindings.pojos.AboutV3;

/**
 */
@Component
public class H2oClusterDAO implements ClusterDAO, H2oConnector {

    @Override
    public boolean tryConnect(String connectionName) {
        try {
            AboutV3 about = this.createH2o(connectionName).about();
            return true;
        } catch (IOException | IllegalArgumentException ioe) {
            return false;
        }
    }

    @Override
    public Collection<String> getConnectionNames() {
        return H2oUtil.getH2oConnectionNames();
    }

}
