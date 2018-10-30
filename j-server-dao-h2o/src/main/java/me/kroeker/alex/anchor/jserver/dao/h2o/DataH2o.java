package me.kroeker.alex.anchor.jserver.dao.h2o;

import de.goerke.tobias.anchorj.tabular.TabularInstance;
import me.kroeker.alex.anchor.h2o.util.DataUtil;
import me.kroeker.alex.anchor.h2o.util.H2oUtil;
import me.kroeker.alex.anchor.jserver.dao.DataDAO;
import me.kroeker.alex.anchor.jserver.dao.exceptions.DataAccessException;
import me.kroeker.alex.anchor.jserver.model.*;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import water.bindings.H2oApi;
import water.bindings.pojos.ColV3;
import water.bindings.pojos.FrameKeyV3;
import water.bindings.pojos.FrameV3;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * @author ak902764
 */
@Component
public class DataH2o extends BaseH2oAccess implements DataDAO {

    private static final Logger LOG = LoggerFactory.getLogger(DataH2o.class);
    private static final int DOMAIN_MAX_ITEMS = 20;

    @Override
    public Map<String, Collection<? extends CaseSelectCondition>> caseSelectConditions(String connectionName, String frameId)
            throws DataAccessException {
        Collection<ColumnSummary<?>> columns = this.getFrame(connectionName, frameId).getColumn_summary_list();

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

    @Override
    public FrameSummary getFrame(String connectionName, String frameId) throws DataAccessException {
        FrameKeyV3 frameKey = new FrameKeyV3();
        frameKey.name = frameId;

        File dataSet = null;
        try {
            H2oApi api = H2oUtil.createH2o(connectionName);

            dataSet = H2oUtil.downloadDataSet(frameKey, api);

            FrameV3 h2oFrame = api.frameSummary(frameKey).frames[0];
            FrameSummary frame = new FrameSummary();

            frame.setFrame_id(h2oFrame.frameId.name);
            long rowCount = h2oFrame.rows;
            frame.setRow_count(rowCount);

            Collection<ColumnSummary<?>> columns = new ArrayList<>(h2oFrame.columns.length);

            for (ColV3 h2oCol : h2oFrame.columns) {
                if (h2oCol.label.equals("weekday")) {
                    h2oCol.type = "string";
                    h2oCol.stringData = new String[]{""};
                }
                final String columnName = h2oCol.label;
                String columnType = h2oCol.type;
                ColumnSummary column;

                if (H2oUtil.isEnumColumn(columnType)) {
                    column = generateEnumColumnSummary(rowCount, h2oCol);
                } else if (H2oUtil.isStringColumn(columnType)) {
                    column = generateStringColumnSummary(dataSet, h2oCol, rowCount);
                } else {
                    column = generateMetricColumnSummary(h2oCol);
                }

                column.setColumn_type(columnType);
                column.setFrame_id(h2oFrame.frameId.name);
                column.setLabel(columnName);
                column.setMissing_count(h2oCol.missingCount);

                columns.add(column);
            }
            frame.setColumn_summary_list(columns);

            return frame;
        } catch (IOException ioe) {
            throw new DataAccessException("Failed to retrieve frame summary of h2o with connection name: "
                    + connectionName + " and frame id: " + frameId, ioe);
        } finally {
            if (dataSet != null && !dataSet.delete()) {
                dataSet.deleteOnExit();
                LOG.error("failed to delete downloaded data set, try delete on exit: " + dataSet.getAbsolutePath());
            }
        }
    }

    @Override
    public TabularInstance randomInstance(String connectionName, String modelId, String frameId, CaseSelectConditionRequest conditions) throws DataAccessException {
        H2oApi api = H2oUtil.createH2o(connectionName);
        try {
            File dataSet = H2oUtil.downloadDataSet(H2oApi.stringToFrameKey(frameId), api);
            return DataUtil.getRandomInstance(conditions, dataSet);
        } catch (IOException ioe) {
            throw new DataAccessException("Failed to retrieve frame summary of h2o with connection name: "
                    + connectionName + " and frame id: " + frameId, ioe);
        }
    }

    private ColumnSummary generateMetricColumnSummary(ColV3 h2oCol) {
        ColumnSummary column;
        column = new ContinuousColumnSummary();
        column.setData(Arrays.asList(ArrayUtils.toObject(h2oCol.data)));
        ((ContinuousColumnSummary) column).setColumn_min((int) h2oCol.mins[0]);
        ((ContinuousColumnSummary) column).setColumn_max((int) h2oCol.maxs[0]);
        ((ContinuousColumnSummary) column).setMean((int) h2oCol.mean);
        return column;
    }

    private ColumnSummary generateStringColumnSummary(File dataSet, ColV3 h2oCol, long rowCount)
            throws IOException {
        String columnName = h2oCol.label;

        CategoricalColumnSummary<String> column = new CategoricalColumnSummary<>();
        column.setData(Arrays.asList(h2oCol.stringData));

        // TODO refactor download of data set
        Map<String, Integer> dataColumn = new HashMap<>();
        DataUtil.iterateThroughCsvData(dataSet,
                (record) ->
                        countStringColumnCategory(dataColumn, record.get(columnName))
        );

        List<CategoryFreq> categories = new ArrayList<>(dataColumn.size());
        for (Map.Entry<String, Integer> entry : dataColumn.entrySet()) {
            categories.add(new CategoryFreq(entry.getKey(), (double) entry.getValue() / rowCount));
        }
        categories.sort((a, b) -> Double.compare(b.getFreq(), a.getFreq()));
        int domainLength = dataColumn.keySet().size();

        // TODO make maximum configurable
        // allow only 20 items
        if (categories.size() > DOMAIN_MAX_ITEMS) {
            categories = categories.subList(0, DOMAIN_MAX_ITEMS);
            int coveredFreq = 0;
            for (CategoryFreq cat : categories) {
                coveredFreq += cat.getFreq();
            }
            categories.add(new CategoryFreq(
                    (domainLength - DOMAIN_MAX_ITEMS) + " more",
                    1 - (double) coveredFreq / rowCount));
        }

        column.setUnique(domainLength);
        column.setCategories(categories);

        return column;
    }

    private void countStringColumnCategory(Map<String, Integer> columnConditions, String categoryValue) {
        if (columnConditions.containsKey(categoryValue)) {
            columnConditions.put(categoryValue, columnConditions.get(categoryValue) + 1);
        } else {
            columnConditions.put(categoryValue, 1);
        }
    }

    private ColumnSummary generateEnumColumnSummary(long rowCount, ColV3 h2oCol) {
        CategoricalColumnSummary column = new CategoricalColumnSummary<Double>();
        column.setData(Arrays.asList(ArrayUtils.toObject(h2oCol.data)));
        long[] histogramBins = h2oCol.histogramBins;
        String[] domain = h2oCol.domain;

        int domainLength = domain.length;
        double coveredFreq = 0;
        List<CategoryFreq> categories = new ArrayList<>(domainLength);
        for (int i = 0; i < domainLength; i++) {
            double freq = (double) histogramBins[i] / rowCount;
            categories.add(new CategoryFreq(domain[i], freq));
            coveredFreq += freq;
        }
        categories.sort((a, b) -> Double.compare(b.getFreq(), a.getFreq()));

        // TODO make maximum configurable
        // allow only 20 items
        if (categories.size() > DOMAIN_MAX_ITEMS) {
            categories = categories.subList(0, DOMAIN_MAX_ITEMS);
            categories.add(new CategoryFreq(
                    (domainLength - DOMAIN_MAX_ITEMS) + " more",
                    1 - coveredFreq));
        }

        column.setCategories(categories);
        column.setUnique(categories.size());
        return column;
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
