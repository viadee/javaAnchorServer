package me.kroeker.alex.anchor.jserver.business;

import me.kroeker.alex.anchor.jserver.api.exceptions.DataAccessException;
import me.kroeker.alex.anchor.jserver.dao.ClusterDAO;
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
