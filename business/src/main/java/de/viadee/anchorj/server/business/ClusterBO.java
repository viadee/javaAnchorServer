package de.viadee.anchorj.server.business;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import de.viadee.anchorj.server.api.exceptions.DataAccessException;
import de.viadee.anchorj.server.dao.ClusterDAO;
import de.viadee.anchorj.server.model.ConnectionNameListResponse;

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

    public ConnectionNameListResponse getConnectionNames() {
        return new ConnectionNameListResponse(this.clusterDAO.getConnectionNames());
    }

}
