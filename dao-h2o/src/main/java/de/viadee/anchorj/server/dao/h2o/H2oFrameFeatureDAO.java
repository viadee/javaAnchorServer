package de.viadee.anchorj.server.dao.h2o;

import de.viadee.anchorj.server.api.exceptions.DataAccessException;
import de.viadee.anchorj.server.dao.FrameDAO;
import de.viadee.anchorj.server.dao.FrameFeatureDAO;
import de.viadee.anchorj.server.model.CategoricalColumnSummary;
import de.viadee.anchorj.server.model.CategoryFreq;
import de.viadee.anchorj.server.model.ColumnSummary;
import de.viadee.anchorj.server.model.ContinuousColumnSummary;
import de.viadee.anchorj.server.model.FeatureCondition;
import de.viadee.anchorj.server.model.FeatureConditionEnum;
import de.viadee.anchorj.server.model.FeatureConditionMetric;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 */
@Component
public class H2oFrameFeatureDAO implements FrameFeatureDAO {

    private FrameDAO frameDAO;

    public H2oFrameFeatureDAO(@Autowired FrameDAO frameDAO) {
        this.frameDAO = frameDAO;
    }

    @Override
    public Map<String, Collection<? extends FeatureCondition>> getFeatureConditions(String connectionName, String frameId)
            throws DataAccessException {
        Collection<ColumnSummary<?>> columns = this.frameDAO.getFrameSummary(connectionName, frameId).getColumn_summary_list();

        Map<String, Collection<? extends FeatureCondition>> conditions = new HashMap<>();
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

    private Collection<FeatureConditionMetric> computeMetricColumnConditions(ContinuousColumnSummary column) {
        Collection<FeatureConditionMetric> columnConditions = new ArrayList<>();

        // TODO make buckets configurable
        int buckets = 4;
        int columnMin = column.getColumn_min();
        int columnMax = column.getColumn_max();
        double step = ((double) columnMax - columnMin) / buckets;

        for (int i = 0; i < buckets; i++) {
            double conditionMin = columnMin + i * step;
            double conditionMax = columnMin + (i + 1) * step;

            columnConditions.add(new FeatureConditionMetric(column.getLabel(), conditionMin, conditionMax));
        }

        return columnConditions;
    }

    private Collection<FeatureConditionEnum> computeEnumColumnConditions(CategoricalColumnSummary column) {
        Collection<FeatureConditionEnum> columnConditions = new ArrayList<>();

        List<CategoryFreq> categories = column.getCategories();
        for (CategoryFreq category : categories) {
            columnConditions.add(new FeatureConditionEnum(column.getLabel(), category.getName()));
        }

        return columnConditions;
    }

}
