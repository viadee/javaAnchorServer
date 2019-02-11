package de.viadee.anchorj.server.controller;

import javax.ws.rs.core.MediaType;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import de.viadee.anchorj.server.api.ClusterApi;
import de.viadee.anchorj.server.business.ClusterBO;
import de.viadee.anchorj.server.model.ConnectionNameListResponse;
import de.viadee.anchorj.server.model.TryConnectResponse;

/**
 *
 */
@RestController
public class ClusterController implements ClusterApi {

    private ClusterBO clusterBO;

    public ClusterController(@Autowired ClusterBO clusterBO) {
        this.clusterBO = clusterBO;
    }

    @Override
    @RequestMapping(
            path = "/version",
            method = RequestMethod.GET)
    public String getVersion() {
        return this.clusterBO.getVersion();
    }

    @Override
    @RequestMapping(
            path = "/{connectionName}/try_connect",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON)
    public TryConnectResponse tryConnect(@PathVariable String connectionName) {
        boolean canConnect = this.clusterBO.tryConnect(connectionName);
        return new TryConnectResponse(canConnect);
    }

    @Override
    @RequestMapping(
            path = "/",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON)
    public ConnectionNameListResponse getConnectionNames() {
        return this.clusterBO.getConnectionNames();
    }

}
