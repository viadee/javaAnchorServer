package me.kroeker.alex.anchor.jserver.dao.h2o;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.ArrayUtils;
import org.springframework.stereotype.Component;
import me.kroeker.alex.anchor.jserver.api.exceptions.DataAccessException;
import me.kroeker.alex.anchor.jserver.dao.FrameDAO;
import me.kroeker.alex.anchor.jserver.h2o.util.H2oConnector;
import me.kroeker.alex.anchor.jserver.h2o.util.H2oDataUtil;
import me.kroeker.alex.anchor.jserver.h2o.util.H2oFrameDownload;
import me.kroeker.alex.anchor.jserver.h2o.util.H2oUtil;
import me.kroeker.alex.anchor.jserver.model.CategoricalColumnSummary;
import me.kroeker.alex.anchor.jserver.model.CategoryFreq;
import me.kroeker.alex.anchor.jserver.model.ColumnSummary;
import me.kroeker.alex.anchor.jserver.model.ContinuousColumnSummary;
import me.kroeker.alex.anchor.jserver.model.DataFrame;
import me.kroeker.alex.anchor.jserver.model.FeatureConditionsRequest;
import me.kroeker.alex.anchor.jserver.model.FrameInstance;
import me.kroeker.alex.anchor.jserver.model.FrameSummary;
import water.bindings.H2oApi;
import water.bindings.pojos.ColV3;
import water.bindings.pojos.FrameBaseV3;
import water.bindings.pojos.FrameKeyV3;
import water.bindings.pojos.FrameV3;
import water.bindings.pojos.FramesListV3;

/**
 *
 */
@Component
public class H2oFrameDAO implements FrameDAO, H2oConnector {

    private static final int DOMAIN_MAX_ITEMS = 20;

    @Override
    public Collection<DataFrame> getFrames(String connectionName) throws DataAccessException {
        try {
            FramesListV3 h2oFrames = this.createH2o(connectionName).frames();
            Collection<DataFrame> frames = new ArrayList<>(h2oFrames.frames.length);
            for (FrameBaseV3 h2oFrame : h2oFrames.frames) {
                DataFrame frame = new DataFrame();
                frame.setFrame_id(h2oFrame.frameId.name);
                frame.setName(h2oFrame.frameId.name);
                frame.setUrl(h2oFrame.frameId.url);

                frames.add(frame);
            }

            return frames;
        } catch (IOException ioe) {
            throw new DataAccessException("Failed to retrieve frames from " + connectionName + " due to: "
                    + ioe.getMessage(), ioe);
        }
    }

    @Override
    public FrameSummary getFrameSummary(String connectionName, String frameId) throws DataAccessException {
        FrameKeyV3 frameKey = new FrameKeyV3();
        frameKey.name = frameId;

        try (H2oFrameDownload h2oDownload = new H2oFrameDownload()) {
            H2oApi api = this.createH2o(connectionName);

            File dataSet = h2oDownload.getFile(api, frameId);

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
        }
    }

    @Override
    public FrameInstance randomInstance(String connectionName, String frameId, FeatureConditionsRequest conditions) throws DataAccessException {
        H2oApi api = this.createH2o(connectionName);
        try (H2oFrameDownload h2oDownload = new H2oFrameDownload()) {
            File dataSet = h2oDownload.getFile(api, frameId);
            return H2oDataUtil.getRandomInstance(conditions, dataSet);
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
        H2oDataUtil.iterateThroughCsvData(dataSet,
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

}
