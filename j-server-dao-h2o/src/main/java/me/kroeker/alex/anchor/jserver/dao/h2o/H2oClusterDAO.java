package me.kroeker.alex.anchor.jserver.dao.h2o;

import java.io.IOException;
import java.util.Collection;

import org.springframework.stereotype.Component;
import me.kroeker.alex.anchor.h2o.util.H2oUtil;
import me.kroeker.alex.anchor.jserver.api.exceptions.DataAccessException;
import me.kroeker.alex.anchor.jserver.dao.ClusterDAO;
import water.bindings.pojos.AboutV3;

/**
 * @author ak902764
 */
@Component
public class H2oClusterDAO implements ClusterDAO {

    @Override
    public boolean tryConnect(String connectionName) throws DataAccessException {
        try {
            AboutV3 about = H2oUtil.createH2o(connectionName).about();
            return true;
        } catch (IOException | IllegalArgumentException ioe) {
            throw new DataAccessException("Failed to connect to h2o server with connection name: " +
                    connectionName, ioe);
        }
    }

    @Override
    public Collection<String> getConnectionNames() {
        return H2oUtil.getH2oConnectionNames();
    }

}
