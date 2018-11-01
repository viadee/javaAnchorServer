package me.kroeker.alex.anchor.jserver.business;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import me.kroeker.alex.anchor.jserver.api.exceptions.DataAccessException;
import me.kroeker.alex.anchor.jserver.dao.FrameFeatureDAO;
import me.kroeker.alex.anchor.jserver.model.FeatureCondition;
import me.kroeker.alex.anchor.jserver.model.FeatureConditionEnum;
import me.kroeker.alex.anchor.jserver.model.FeatureConditionMetric;
import me.kroeker.alex.anchor.jserver.model.FeatureConditionsResponse;

/**
 */
@Component
public class FrameFeatureBO {

    private FrameFeatureDAO frameFeatureDAO;

    public FrameFeatureBO(@Autowired FrameFeatureDAO frameFeatureDAO) {
        this.frameFeatureDAO = frameFeatureDAO;
    }

    public FeatureConditionsResponse getFeatureConditions(String connectionName,
                                                          String frameId) throws DataAccessException {
        Map<String, Collection<FeatureConditionEnum>> enumConditions = new HashMap<>();
        Map<String, Collection<FeatureConditionMetric>> metricConditions = new HashMap<>();

        Map<String, Collection<? extends FeatureCondition>> conditions = this.frameFeatureDAO.getFeatureConditions(connectionName, frameId);
        for (Map.Entry<String, Collection<? extends FeatureCondition>> condition : conditions.entrySet()) {
            if (condition.getValue() == null || condition.getValue().size() <= 0) {
                continue;
            }
            FeatureCondition firstCondition = condition.getValue().iterator().next();
            if (firstCondition instanceof FeatureConditionEnum) {
                enumConditions.put(condition.getKey(), (Collection<FeatureConditionEnum>) condition.getValue());
            } else if (firstCondition instanceof FeatureConditionMetric) {
                metricConditions.put(condition.getKey(), (Collection<FeatureConditionMetric>) condition.getValue());
            } else {
                throw new IllegalArgumentException("type " + condition.getClass().getSimpleName() + " is not implemented");
            }
        }

        return new FeatureConditionsResponse(enumConditions, metricConditions);
    }

}
