package me.kroeker.alex.anchor.jserver.business;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import me.kroeker.alex.anchor.jserver.api.exceptions.DataAccessException;
import me.kroeker.alex.anchor.jserver.dao.FrameColumnDAO;
import me.kroeker.alex.anchor.jserver.model.CaseSelectCondition;
import me.kroeker.alex.anchor.jserver.model.CaseSelectConditionEnum;
import me.kroeker.alex.anchor.jserver.model.CaseSelectConditionMetric;
import me.kroeker.alex.anchor.jserver.model.CaseSelectConditionResponse;

/**
 */
@Component
public class FrameColumnBO {

    private FrameColumnDAO frameColumnDAO;

    public FrameColumnBO(@Autowired FrameColumnDAO frameColumnDAO) {
        this.frameColumnDAO = frameColumnDAO;
    }

    public CaseSelectConditionResponse caseSelectConditions(String connectionName,
                                                            String frameId) throws DataAccessException {
        Map<String, Collection<CaseSelectConditionEnum>> enumConditions = new HashMap<>();
        Map<String, Collection<CaseSelectConditionMetric>> metricConditions = new HashMap<>();

        Map<String, Collection<? extends CaseSelectCondition>> conditions = this.frameColumnDAO.caseSelectConditions(connectionName, frameId);
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
    }

}
