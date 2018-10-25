package me.kroeker.alex.anchor.jserver.dao.h2o;

import me.kroeker.alex.anchor.jserver.dao.DataDAO;
import me.kroeker.alex.anchor.jserver.dao.exceptions.DataAccessException;
import me.kroeker.alex.anchor.jserver.model.*;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.stereotype.Component;
import water.bindings.pojos.ColV3;
import water.bindings.pojos.FrameKeyV3;
import water.bindings.pojos.FrameV3;

import java.io.IOException;
import java.util.*;

/**
 * @author ak902764
 */
@Component
public class DataH2o extends BaseH2oAccess implements DataDAO {

    @Override
    public Map<String, Map<Integer, String>> caseSelectConditions(String connectionName, String frameId)
            throws DataAccessException {
        Collection<ColumnSummary<?>> columns = this.getFrame(connectionName, frameId).getColumn_summary_list();

        Map<String, Map<Integer, String>> conditions = new HashMap<>();
        for (ColumnSummary column : columns) {
            Map<Integer, String> columnConditions = new HashMap<>();
            String featureName = column.getLabel();

            if ("enum".equals(column.getColumn_type()) || "string".equals(column.getColumn_type())) {
                List<CategoryFreq> categories = ((CategoricalColumnSummary) column).getCategories();
                for (int i = 0; i < categories.size(); i++) {
                    columnConditions.put(i, categories.get(i).getName());
                }
            } else {
                int buckets = 4;
                ContinuousColumnSummary cColumn = (ContinuousColumnSummary) column;
                int columnMin = cColumn.getColumn_min();
                int columnMax = cColumn.getColumn_max();
                double step = ((double) columnMax - columnMin) / buckets;

                for (int i = 0; i < buckets; i++) {
                    double conditionMin = columnMin + i * step;
                    double conditionMax = columnMin + (i + 1) * step;

                    columnConditions.put(i, conditionMin + " <= " + featureName + " < " + conditionMax); // '{} <= {} < {}'.format(condition_min, column.label, condition_max));
                }
            }

            conditions.put(featureName, columnConditions);
        }
        return conditions;
    }

    @Override
    public FrameSummary getFrame(String connectionName, String frameId) throws DataAccessException {
        FrameKeyV3 frameKey = new FrameKeyV3();
        frameKey.name = frameId;
        try {
            // TODO frame summary causes MalformedJsonException due to "NaN" value for value mean when column type
            // TODO is enum or string
            FrameV3 h2oFrame = this.createH2o(connectionName).frameSummary(frameKey).frames[0];
            FrameSummary frame = new FrameSummary();

            frame.setFrame_id(h2oFrame.frameId.name);
            int rowCount = h2oFrame.rowCount;
            frame.setRow_count(rowCount);

            Collection<ColumnSummary<?>> columns = new ArrayList<>(h2oFrame.columns.length);
            for (ColV3 h2oCol : h2oFrame.columns) {
                String columnType = h2oCol.type;
                ColumnSummary column;

                if ("enum".equals(columnType)) {
                    column = new CategoricalColumnSummary();
                    column.setData(Arrays.asList(ArrayUtils.toObject(h2oCol.data)));
                    long[] histogramBins = h2oCol.histogramBins;
                    String[] domain = h2oCol.domain;

                    int domainLength = domain.length;
                    int domainMaxItems = 20;
                    double freqCount = 0;
                    List<CategoryFreq> categories = new ArrayList<>(domainLength);
                    for (int i = 0; i < domainLength; i++) {
                        double freq = histogramBins[i] / (double) rowCount;
                        categories.add(new CategoryFreq(domain[i], freq));
                        freqCount += freq;

                        // TODO freq count is wrong
                        if (categories.size() > domainMaxItems) {
                            categories.add(new CategoryFreq(
                                    (domainLength - domainMaxItems) + " more",
                                    1 - freqCount));
                            break;
                        }
                    }
                    categories.sort((a, b) -> (int) (a.getFreq() - b.getFreq()));

                    ((CategoricalColumnSummary) column).setCategories(categories);
                    ((CategoricalColumnSummary) column).setUnique(categories.size());
                } else if ("string".equals(columnType) || "uuid".equals(columnType)) {
                    column = new CategoricalColumnSummary();
                    column.setData(Arrays.asList(h2oCol.stringData));
                    Set<String> uniqueStrings = new HashSet<String>(column.getData());
                    ((CategoricalColumnSummary) column).setUnique(uniqueStrings.size());
                    // TODO tail is missing

                } else {
                    column = new ContinuousColumnSummary();
                    column.setData(Arrays.asList(ArrayUtils.toObject(h2oCol.data)));
                    ((ContinuousColumnSummary) column).setColumn_min((int) h2oCol.mins[0]);
                    ((ContinuousColumnSummary) column).setColumn_max((int) h2oCol.maxs[0]);
                    ((ContinuousColumnSummary) column).setMean((int) h2oCol.mean);
                }

                column.setColumn_type(columnType);
                column.setFrame_id(h2oFrame.frameId.name);
                column.setLabel(h2oCol.label);
                column.setMissing_count(h2oCol.missingCount);

                columns.add(column);
            }
            frame.setColumn_summary_list(columns);

            return frame;
        } catch (IOException ioe) {
            throw new DataAccessException("Failed to retrieve frame summary of h2o with connection name: "
                    + connectionName + " and frame id: " + frameId, ioe);
        }
    }

}
