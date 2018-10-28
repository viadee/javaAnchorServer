package me.kroeker.alex.anchor.jserver.controller;

import java.util.ArrayList;
import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import me.kroeker.alex.anchor.jserver.api.RuleApi;
import me.kroeker.alex.anchor.jserver.dao.RuleDAO;
import me.kroeker.alex.anchor.jserver.model.CaseSelectCondition;
import me.kroeker.alex.anchor.jserver.model.CaseSelectConditionEnum;
import me.kroeker.alex.anchor.jserver.model.CaseSelectConditionResponse;
import me.kroeker.alex.anchor.jserver.model.Rule;

/**
 * @author ak902764
 */
@RestController
public class RuleController implements RuleApi {

    @Autowired
    private RuleDAO ruleDAO;

    @Override
    @RequestMapping(value = "/{connectionName}/rule/{modelId}/{frameId}", method = RequestMethod.POST, produces = {
            "application/json" })
    public Rule createRule(@PathVariable String connectionName, @PathVariable String modelId, @PathVariable String frameId,
                           CaseSelectConditionResponse conditions) {
        Collection<CaseSelectCondition> conditionsList = new ArrayList<>();
        if (conditions.getEnumConditions() != null) {
            conditions.getEnumConditions().values().forEach(conditionsList::addAll);
        }
        if (conditions.getMetricConditions() != null) {
            conditions.getMetricConditions().values().forEach(conditionsList::addAll);
        }
        return ruleDAO.randomRule(modelId, frameId, conditionsList);
    }

}
