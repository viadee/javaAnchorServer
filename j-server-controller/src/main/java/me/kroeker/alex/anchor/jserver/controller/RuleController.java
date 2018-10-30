package me.kroeker.alex.anchor.jserver.controller;

import de.goerke.tobias.anchorj.tabular.TabularInstance;
import me.kroeker.alex.anchor.jserver.api.RuleApi;
import me.kroeker.alex.anchor.jserver.api.exceptions.DataAccessException;
import me.kroeker.alex.anchor.jserver.business.DataBO;
import me.kroeker.alex.anchor.jserver.business.RuleBO;
import me.kroeker.alex.anchor.jserver.model.CaseSelectConditionRequest;
import me.kroeker.alex.anchor.jserver.model.Rule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.ws.rs.core.MediaType;

/**
 * @author ak902764
 */
@RestController
public class RuleController implements RuleApi {

    private static final Logger LOG = LoggerFactory.getLogger(RuleController.class);

    private DataBO dataBO;

    private RuleBO ruleBO;

    public RuleController(@Autowired DataBO dataBO, @Autowired RuleBO ruleBO) {
        this.dataBO = dataBO;
        this.ruleBO = ruleBO;
    }

    @Override
    @RequestMapping(
            value = "/{connectionName}/rule/{modelId}/{frameId}",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON,
            consumes = MediaType.APPLICATION_JSON
    )
    public Rule createRule(
            @PathVariable String connectionName,
            @PathVariable String modelId,
            @PathVariable String frameId,
            @RequestBody CaseSelectConditionRequest conditions
    ) {
        try {
            TabularInstance instance = dataBO.randomInstance(connectionName, modelId, frameId, conditions);

            return this.ruleBO.computeRule(connectionName, modelId, frameId, instance);
        } catch (DataAccessException dae) {
            LOG.error(dae.getMessage(), dae);
            // TODO add exception handling
            return null;
        }
    }

}
