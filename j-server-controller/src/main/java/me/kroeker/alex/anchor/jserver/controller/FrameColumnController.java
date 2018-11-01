package me.kroeker.alex.anchor.jserver.controller;

import javax.ws.rs.core.MediaType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import me.kroeker.alex.anchor.jserver.api.FrameColumnApi;
import me.kroeker.alex.anchor.jserver.api.exceptions.DataAccessException;
import me.kroeker.alex.anchor.jserver.business.FrameColumnBO;
import me.kroeker.alex.anchor.jserver.model.CaseSelectConditionResponse;

/**
 */
@RestController
public class FrameColumnController implements FrameColumnApi {

    private static final Logger LOG = LoggerFactory.getLogger(FrameColumnController.class);

    private final FrameColumnBO frameColumnBO;

    public FrameColumnController(@Autowired FrameColumnBO frameColumnBO) {
        this.frameColumnBO = frameColumnBO;
    }

    @Override
    @RequestMapping(
            value = "/{connectionName}/frames/{frameId}/conditions",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON
    )
    public CaseSelectConditionResponse getCaseSelectConditions(
            @PathVariable String connectionName,
            @PathVariable String frameId) {
        try {
            return this.frameColumnBO.caseSelectConditions(connectionName, frameId);
        } catch (DataAccessException dae) {
            LOG.error(dae.getMessage(), dae);
            // TODO add exception handling
            return null;
        }
    }

}
