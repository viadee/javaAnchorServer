package de.viadee.anchorj.server.business;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import de.viadee.anchorj.server.configuration.AppConfiguration;
import de.viadee.anchorj.server.dao.ClusterDAO;
import de.viadee.anchorj.server.model.ConnectionNameListResponse;

/**
 *
 */
@Component
public class ClusterBO {

    private AppConfiguration configuration;

    private ClusterDAO clusterDAO;

    public ClusterBO(@Autowired AppConfiguration configuration, @Autowired ClusterDAO clusterDAO) {
        this.configuration = configuration;
        this.clusterDAO = clusterDAO;
    }

    public String getVersion() {
        return this.configuration.getVersion();
    }

    public boolean tryConnect(String connectionName) {
        return this.clusterDAO.tryConnect(connectionName);
    }

    public ConnectionNameListResponse getConnectionNames() {
        return new ConnectionNameListResponse(this.clusterDAO.getConnectionNames());
    }

}
