package me.kroeker.alex.anchor.jserver.dao.h2o;

import java.io.IOException;
import java.util.Collection;

import org.springframework.stereotype.Component;
import me.kroeker.alex.anchor.h2o.util.H2oUtil;
import me.kroeker.alex.anchor.jserver.dao.ClusterDAO;
import water.bindings.pojos.AboutV3;

/**
 */
@Component
public class H2oClusterDAO implements ClusterDAO {

    @Override
    public boolean tryConnect(String connectionName) {
        try {
            AboutV3 about = H2oUtil.createH2o(connectionName).about();
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
