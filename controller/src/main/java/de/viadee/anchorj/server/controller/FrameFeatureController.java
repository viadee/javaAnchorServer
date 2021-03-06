package de.viadee.anchorj.server.controller;

import de.viadee.anchorj.server.api.FrameFeatureApi;
import de.viadee.anchorj.server.api.exceptions.DataAccessException;
import de.viadee.anchorj.server.business.FrameFeatureBO;
import de.viadee.anchorj.server.model.FeatureConditionsResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.ws.rs.core.MediaType;

/**
 */
@RestController
public class FrameFeatureController implements FrameFeatureApi {

    private static final Logger LOG = LoggerFactory.getLogger(FrameFeatureController.class);

    private final FrameFeatureBO frameFeatureBO;

    public FrameFeatureController(@Autowired FrameFeatureBO frameFeatureBO) {
        this.frameFeatureBO = frameFeatureBO;
    }

    @Override
    @RequestMapping(
            value = "/{connectionName}/frames/{frameId}/conditions",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON
    )
    public FeatureConditionsResponse getFeatureConditions(
            @PathVariable String connectionName,
            @PathVariable String frameId) {
        try {
            return this.frameFeatureBO.getFeatureConditions(connectionName, frameId);
        } catch (DataAccessException dae) {
            LOG.error(dae.getMessage(), dae);
            // TODO add exception handling
            return null;
        }
    }

}
