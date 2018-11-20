package me.kroeker.alex.anchor.jserver.controller;

import me.kroeker.alex.anchor.jserver.api.ClusterApi;
import me.kroeker.alex.anchor.jserver.api.exceptions.DataAccessException;
import me.kroeker.alex.anchor.jserver.business.ClusterBO;
import me.kroeker.alex.anchor.jserver.model.TryConnectResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.ws.rs.core.MediaType;
import java.util.Collection;

/**
 */
@RestController
public class ClusterController implements ClusterApi {

    private static final Logger LOG = LoggerFactory.getLogger(ClusterController.class);

    private ClusterBO clusterBO;

    public ClusterController(@Autowired ClusterBO clusterBO) {
        this.clusterBO = clusterBO;
    }

    @Override
    @RequestMapping(
            path = "/{connectionName}/try_connect",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON)
    public TryConnectResponse tryConnect(@PathVariable String connectionName) {
        try {
            boolean canConnect = this.clusterBO.tryConnect(connectionName);
            return new TryConnectResponse(canConnect);
        } catch (DataAccessException dae) {
            LOG.error(dae.getMessage(), dae);
            // TODO add exception handling
            return null;
        }
    }

    @Override
    @RequestMapping(
            path = "/",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON)
    public Collection<String> getConnectionNames() {
        return this.clusterBO.getConnectionNames();
    }

}
