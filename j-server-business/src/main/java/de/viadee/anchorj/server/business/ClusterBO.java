package de.viadee.anchorj.server.business;

import de.viadee.anchorj.server.api.exceptions.DataAccessException;
import de.viadee.anchorj.server.dao.ClusterDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collection;

/**
 */
@Component
public class ClusterBO {

    private ClusterDAO clusterDAO;

    public ClusterBO(@Autowired ClusterDAO clusterDAO) {
        this.clusterDAO = clusterDAO;
    }

    public boolean tryConnect(String connectionName) throws DataAccessException {
        return this.clusterDAO.tryConnect(connectionName);
    }

    public Collection<String> getConnectionNames() {
        return this.clusterDAO.getConnectionNames();
    }
}
