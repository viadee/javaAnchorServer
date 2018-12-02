package de.viadee.anchorj.server.controller;

import de.viadee.anchorj.server.api.FrameApi;
import de.viadee.anchorj.server.api.exceptions.DataAccessException;
import de.viadee.anchorj.server.business.FrameBO;
import de.viadee.anchorj.server.model.DataFrame;
import de.viadee.anchorj.server.model.FrameSummary;
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
public class FrameController implements FrameApi {

    private static final Logger LOG = LoggerFactory.getLogger(FrameController.class);

    private FrameBO frameBO;

    public FrameController(@Autowired FrameBO frameBO) {
        this.frameBO = frameBO;
    }

    @Override
    @RequestMapping(
            path = "/{connectionName}/frames",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON
    )
    public Collection<DataFrame> getFrames(@PathVariable String connectionName) {
        try {
            return this.frameBO.getFrames(connectionName);
        } catch (DataAccessException dae) {
            LOG.error(dae.getMessage(), dae);
            // TODO add exception handling
            return null;
        }
    }

    @Override
    @RequestMapping(
            value = "/{connectionName}/frames/{frameId}/summary",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON
    )
    public FrameSummary getFrameSummary(@PathVariable String connectionName, @PathVariable String frameId) {
        try {
            return this.frameBO.getFrameSummary(connectionName, frameId);
        } catch (DataAccessException dae) {
            LOG.error(dae.getMessage(), dae);
            // TODO add exception handling
            return null;
        }
    }

}
