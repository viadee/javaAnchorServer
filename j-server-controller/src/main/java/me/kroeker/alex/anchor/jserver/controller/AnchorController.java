package me.kroeker.alex.anchor.jserver.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
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
import me.kroeker.alex.anchor.jserver.api.AnchorApi;
import me.kroeker.alex.anchor.jserver.api.exceptions.DataAccessException;
import me.kroeker.alex.anchor.jserver.business.AnchorBO;
import me.kroeker.alex.anchor.jserver.business.FrameBO;
import me.kroeker.alex.anchor.jserver.model.Anchor;
import me.kroeker.alex.anchor.jserver.model.AnchorConfigDescription;
import me.kroeker.alex.anchor.jserver.model.FeatureConditionsRequest;
import me.kroeker.alex.anchor.jserver.model.FrameInstance;
import me.kroeker.alex.anchor.jserver.model.SubmodularPickResult;

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
            File anchorsSer = new File("test-anchors" + modelId + ".obj");
            boolean cache = false;
            if (cache && anchorsSer.exists() && anchorsSer.length() > 0) {
                try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(anchorsSer))) {
                    return (SubmodularPickResult) ois.readObject();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            FrameInstance instance = this.frameBO.randomInstance(connectionName, frameId);

            Collection<AnchorConfigDescription> configDescription = this.anchorBO.getAnchorConfigs();
            SubmodularPickResult anchors = this.anchorBO.runSubmodularPick(
                    connectionName,
                    modelId,
                    frameId,
                    instance,
                    this.getAnchorConfig(configDescription)
            );
            if (cache) {
                try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(anchorsSer))) {
                    oos.writeObject(anchors);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return anchors;
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
