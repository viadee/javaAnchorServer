package me.kroeker.alex.anchor.jserver.business;

import de.goerke.tobias.anchorj.tabular.TabularInstance;
import me.kroeker.alex.anchor.jserver.api.exceptions.DataAccessException;
import me.kroeker.alex.anchor.jserver.dao.DataDAO;
import me.kroeker.alex.anchor.jserver.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Component
public class DataBO {

    @Autowired
    private DataDAO dataDAO;

    public FrameSummary getFrame(String connectionName, String frameId) throws DataAccessException {
        return this.dataDAO.getFrame(connectionName, frameId);
    }

    public CaseSelectConditionResponse caseSelectConditions(String connectionName,
                                                            String frameId) throws DataAccessException {
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
    }

    public TabularInstance randomInstance(String connectionName,
                                          String modelId,
                                          String frameId,
                                          CaseSelectConditionRequest conditions) throws DataAccessException {
        return this.dataDAO.randomInstance(connectionName, modelId, frameId, conditions);
    }

}
