package me.kroeker.alex.anchor.jserver.dao.h2o;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import me.kroeker.alex.anchor.jserver.api.exceptions.DataAccessException;
import me.kroeker.alex.anchor.jserver.dao.FrameColumnDAO;
import me.kroeker.alex.anchor.jserver.dao.FrameDAO;
import me.kroeker.alex.anchor.jserver.model.CaseSelectCondition;
import me.kroeker.alex.anchor.jserver.model.CaseSelectConditionEnum;
import me.kroeker.alex.anchor.jserver.model.CaseSelectConditionMetric;
import me.kroeker.alex.anchor.jserver.model.CategoricalColumnSummary;
import me.kroeker.alex.anchor.jserver.model.CategoryFreq;
import me.kroeker.alex.anchor.jserver.model.ColumnSummary;
import me.kroeker.alex.anchor.jserver.model.ContinuousColumnSummary;

/**
 */
@Component
public class H2oFrameColumnDAO implements FrameColumnDAO {

    private FrameDAO frameDAO;

    public H2oFrameColumnDAO(@Autowired FrameDAO frameDAO) {
        this.frameDAO = frameDAO;
    }

    @Override
    public Map<String, Collection<? extends CaseSelectCondition>> caseSelectConditions(String connectionName, String frameId)
            throws DataAccessException {
        Collection<ColumnSummary<?>> columns = this.frameDAO.getFrameSummary(connectionName, frameId).getColumn_summary_list();

        Map<String, Collection<? extends CaseSelectCondition>> conditions = new HashMap<>();
        for (ColumnSummary column : columns) {
            String featureName = column.getLabel();
            // TODO strings as global constants
            if ("enum".equals(column.getColumn_type()) || "string".equals(column.getColumn_type())) {
                conditions.put(featureName, computeEnumColumnConditions((CategoricalColumnSummary) column));
            } else {
                conditions.put(featureName, computeMetricColumnConditions((ContinuousColumnSummary) column));
            }

        }
        return conditions;
    }

    private Collection<CaseSelectConditionMetric> computeMetricColumnConditions(ContinuousColumnSummary column) {
        Collection<CaseSelectConditionMetric> columnConditions = new ArrayList<>();

        // TODO make buckets configurable
        int buckets = 4;
        int columnMin = column.getColumn_min();
        int columnMax = column.getColumn_max();
        double step = ((double) columnMax - columnMin) / buckets;

        for (int i = 0; i < buckets; i++) {
            double conditionMin = columnMin + i * step;
            double conditionMax = columnMin + (i + 1) * step;

            columnConditions.add(new CaseSelectConditionMetric(column.getLabel(), conditionMin, conditionMax));
        }

        return columnConditions;
    }

    private Collection<CaseSelectConditionEnum> computeEnumColumnConditions(CategoricalColumnSummary column) {
        Collection<CaseSelectConditionEnum> columnConditions = new ArrayList<>();

        List<CategoryFreq> categories = column.getCategories();
        for (CategoryFreq category : categories) {
            columnConditions.add(new CaseSelectConditionEnum(column.getLabel(), category.getName()));
        }

        return columnConditions;
    }

}
