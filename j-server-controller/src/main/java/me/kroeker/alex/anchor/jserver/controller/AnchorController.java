package me.kroeker.alex.anchor.jserver.controller;

import javax.ws.rs.core.MediaType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import de.goerke.tobias.anchorj.tabular.TabularInstance;
import me.kroeker.alex.anchor.jserver.api.AnchorApi;
import me.kroeker.alex.anchor.jserver.api.exceptions.DataAccessException;
import me.kroeker.alex.anchor.jserver.business.AnchorBO;
import me.kroeker.alex.anchor.jserver.business.FrameBO;
import me.kroeker.alex.anchor.jserver.model.Anchor;
import me.kroeker.alex.anchor.jserver.model.FeatureConditionsRequest;

/**
 */
@RestController
public class AnchorController implements AnchorApi {

    private static final Logger LOG = LoggerFactory.getLogger(AnchorController.class);

    private FrameBO frameBO;

    private AnchorBO anchorBO;

    public AnchorController(@Autowired FrameBO frameBO, @Autowired AnchorBO anchorBO) {
        this.frameBO = frameBO;
        this.anchorBO = anchorBO;
    }

    @Override
    @RequestMapping(
            value = "/{connectionName}/anchors",
            method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_JSON,
            produces = MediaType.APPLICATION_JSON
    )
    public Anchor computeAnchor(
            @PathVariable String connectionName,
            @RequestHeader("Model-Id") String modelId,
            @RequestHeader("Frame-Id") String frameId,
            @RequestBody FeatureConditionsRequest conditions
    ) {
        try {
            TabularInstance instance = this.frameBO.randomInstance(connectionName, frameId, conditions);

            return this.anchorBO.computeRule(connectionName, modelId, frameId, instance);
        } catch (DataAccessException dae) {
            LOG.error(dae.getMessage(), dae);
            // TODO add exception handling
            return null;
        }
    }

}
