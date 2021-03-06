package de.viadee.anchorj.server.controller;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.MediaType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import de.viadee.anchorj.server.api.AnchorApi;
import de.viadee.anchorj.server.api.exceptions.DataAccessException;
import de.viadee.anchorj.server.business.AnchorBO;
import de.viadee.anchorj.server.business.FrameBO;
import de.viadee.anchorj.server.model.Anchor;
import de.viadee.anchorj.server.model.AnchorConfigDescription;
import de.viadee.anchorj.server.model.FeatureConditionsRequest;
import de.viadee.anchorj.server.model.FrameInstance;
import de.viadee.anchorj.server.model.SubmodularPickResult;

/**
 */
@RestController
public class AnchorController implements AnchorApi {

    private static final Logger LOG = LoggerFactory.getLogger(AnchorController.class);

    private final FrameBO frameBO;

    private final AnchorBO anchorBO;

    private final HttpServletRequest request;

    public AnchorController(@Autowired HttpServletRequest request, @Autowired FrameBO frameBO, @Autowired AnchorBO anchorBO) {
        this.request = request;
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
            FrameInstance instance = this.frameBO.randomInstance(connectionName, frameId, conditions);

            Collection<AnchorConfigDescription> configDescription = this.anchorBO.getAnchorConfigs();
            return this.anchorBO.computeRule(
                    connectionName,
                    modelId,
                    frameId,
                    instance,
                    this.getAnchorConfig(configDescription)
            );
        } catch (DataAccessException dae) {
            LOG.error(dae.getMessage(), dae);
            // TODO add exception handling
            return null;
        }
    }

    @Override
    @RequestMapping(
            value = "/{connectionName}/anchors/global",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON
    )
    public SubmodularPickResult runSubmodularPick(
            @PathVariable String connectionName,
            @RequestHeader("Model-Id") String modelId,
            @RequestHeader("Frame-Id") String frameId
    ) {
        try {
            FrameInstance instance = this.frameBO.randomInstance(connectionName, frameId);

            Collection<AnchorConfigDescription> configDescription = this.anchorBO.getAnchorConfigs();
            return this.anchorBO.runSubmodularPick(
                    connectionName,
                    modelId,
                    frameId,
                    instance,
                    this.getAnchorConfig(configDescription)
            );
        } catch (DataAccessException dae) {
            LOG.error(dae.getMessage(), dae);
            // TODO add exception handling
            return null;
        }
    }

    @Override
    @RequestMapping(
            value = "/anchors/config",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON
    )
    public Collection<AnchorConfigDescription> getAnchorConfigs() {
        try {
            return this.anchorBO.getAnchorConfigs();
        } catch (DataAccessException dae) {
            LOG.error(dae.getMessage(), dae);
            // TODO add exception handling
            return null;
        }
    }

    private Map<String, Object> getAnchorConfig(Collection<AnchorConfigDescription> configDescription) {
        Map<String, Object> anchorConfig = new HashMap<>();
        for (AnchorConfigDescription config : configDescription) {
            String headerValue = this.request.getHeader(config.getConfigName());
            if (headerValue != null && !headerValue.isEmpty()) {
                switch (config.getInputType()) {
                    case DOUBLE:
                        anchorConfig.put(config.getConfigName(), Double.valueOf(headerValue));
                        break;
                    case STRING:
                        anchorConfig.put(config.getConfigName(), headerValue);
                        break;
                    case INTEGER:
                        anchorConfig.put(config.getConfigName(), Integer.valueOf(headerValue));
                        break;
                    default:
                        throw new RuntimeException("input type " + config.getInputType() + " is not handled");
                }
            }
        }

        return anchorConfig;
    }

}
