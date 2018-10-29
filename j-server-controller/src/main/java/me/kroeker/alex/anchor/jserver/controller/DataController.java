package me.kroeker.alex.anchor.jserver.controller;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.core.MediaType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import me.kroeker.alex.anchor.jserver.dao.DataDAO;
import me.kroeker.alex.anchor.jserver.dao.exceptions.DataAccessException;
import me.kroeker.alex.anchor.jserver.model.CaseSelectCondition;
import me.kroeker.alex.anchor.jserver.model.CaseSelectConditionEnum;
import me.kroeker.alex.anchor.jserver.model.CaseSelectConditionMetric;
import me.kroeker.alex.anchor.jserver.model.CaseSelectConditionResponse;
import me.kroeker.alex.anchor.jserver.model.FrameSummary;
import me.kroeker.alex.anchor.jserver.api.DataApi;

/**
 * @author ak902764
 */
@RestController
public class DataController implements DataApi {

    private static final Logger LOG = LoggerFactory.getLogger(DataController.class);

    @Autowired
    private DataDAO dataDAO;

    @Override
    @RequestMapping(
            value = "/{connectionName}/frames/{frameId}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON
    )
    public FrameSummary getFrame(@PathVariable String connectionName, @PathVariable String frameId) {
        try {
            return this.dataDAO.getFrame(connectionName, frameId);
        } catch (DataAccessException dae) {
            LOG.error(dae.getMessage(), dae);
            // TODO add exception handling
            return null;
        }
    }

    @Override
    @RequestMapping(
            value = "/{connectionName}/frames/{frameId}/conditions",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON
    )
    public CaseSelectConditionResponse caseSelectConditions(@PathVariable String connectionName,
                                                            @PathVariable String frameId) {
        try {
            Map<String, Collection<CaseSelectConditionEnum>> enumConditions = new HashMap<>();
            Map<String, Collection<CaseSelectConditionMetric>> metricConditions = new HashMap<>();

            Map<String, Collection<? extends CaseSelectCondition>> conditions = this.dataDAO.caseSelectConditions(connectionName, frameId);
            for (Map.Entry<String, Collection<? extends CaseSelectCondition>> condition : conditions.entrySet()) {
                if (condition.getValue() == null || condition.getValue().size() <= 0) {
                    continue;
                }
                CaseSelectCondition firstCondition = condition.getValue().iterator().next();
                if (firstCondition instanceof CaseSelectConditionEnum) {
                    enumConditions.put(condition.getKey(), (Collection<CaseSelectConditionEnum>) condition.getValue());
                } else if (firstCondition instanceof CaseSelectConditionMetric) {
                    metricConditions.put(condition.getKey(), (Collection<CaseSelectConditionMetric>) condition.getValue());
                } else {
                    throw new IllegalArgumentException("type " + condition.getClass().getSimpleName() + " is not implemented");
                }
            }

            return new CaseSelectConditionResponse(enumConditions, metricConditions);
        } catch (DataAccessException dae) {
            LOG.error(dae.getMessage(), dae);
            // TODO add exception handling
            return null;
        }
    }

}
