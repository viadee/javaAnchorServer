package de.viadee.anchorj.server.h2o.util;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Consumer;
import java.util.function.Function;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import de.viadee.anchorj.server.api.exceptions.DataAccessException;
import de.viadee.anchorj.server.model.FeatureConditionEnum;
import de.viadee.anchorj.server.model.FeatureConditionMetric;
import de.viadee.anchorj.server.model.FeatureConditionsRequest;
import de.viadee.anchorj.server.model.FrameInstance;
import water.bindings.H2oApi;

public final class H2oDataUtil {

    private H2oDataUtil() {
    }

    public static FrameInstance getRandomInstance(FeatureConditionsRequest conditions, File dataSet) throws IOException {
        Collection<Function<CSVRecord, Boolean>> filters = calculateConditionsFilter(conditions);

        List<CSVRecord> acceptedRecords = new ArrayList<>();
        Map<String, Integer> headerMapping = H2oDataUtil.iterateThroughCsvData(dataSet,
                (record) -> {
                    if (filters.isEmpty() || filters.stream().allMatch(filter -> filter.apply(record))) {
                        acceptedRecords.add(record);
                    }
                }
        );

        int randomNum = ThreadLocalRandom.current().nextInt(0, acceptedRecords.size());
        CSVRecord acceptedInstance = acceptedRecords.get(randomNum);

        String[] acceptedInstanceString = new String[headerMapping.size()];
        List<Map.Entry<String, Integer>> headList = new ArrayList<>(headerMapping.size());
        headList.addAll(headerMapping.entrySet());
        headList.sort(Comparator.comparingInt(Map.Entry::getValue));
        headList.forEach((entry) -> {
            acceptedInstanceString[entry.getValue()] = acceptedInstance.get(entry.getKey());
        });

        return new FrameInstance(headerMapping, acceptedInstanceString);
    }

    private static Collection<Function<CSVRecord, Boolean>> calculateConditionsFilter(FeatureConditionsRequest conditions) {
        Collection<Function<CSVRecord, Boolean>> filters = new ArrayList<>();
        if (conditions != null) {
            if (conditions.getEnumConditions() != null) {
                for (FeatureConditionEnum enumCondition : conditions.getEnumConditions().values()) {
                    filters.add(
                            (record) ->
                                    record.get(enumCondition.getFeatureName()).equals(enumCondition.getCategory())
                    );
                }
            }
            // TODO handle metrics min max problem. wenn das maximum 20 ist und 20 als condition, sollte 20 inklusive sein
            if (conditions.getMetricConditions() != null) {
                for (FeatureConditionMetric metricCondition : conditions.getMetricConditions().values()) {
                    filters.add(
                            (record) -> {
                                double recordValue = Double.parseDouble(record.get(metricCondition.getFeatureName()));
                                return metricCondition.getConditionMin() < recordValue && recordValue < metricCondition.getConditionMax();
                            }
                    );
                }
            }
        }
        return filters;
    }

    public static Map<String, Integer> iterateThroughCsvData(File file, Consumer<CSVRecord> recordConsumer) throws IOException {
        try (Reader in = new FileReader(file)) {
            CSVParser records = CSVFormat.DEFAULT.withFirstRecordAsHeader().parse(in);

            for (CSVRecord record : records) {
                recordConsumer.accept(record);
            }

            return records.getHeaderMap();
        }
    }

    public static Map<String, Integer> loadDataSetFromH2o(String frameId, H2oApi api, List<String[]> dataSetResponse) throws DataAccessException {
        Map<String, Integer> header;
        try (H2oFrameDownload h2oDownload = new H2oFrameDownload()) {
            File dataSetFile = h2oDownload.getFile(api, frameId);
            Collection<String> newLine = new ArrayList<>();
            header = H2oDataUtil.iterateThroughCsvData(dataSetFile, (record) -> {
                        newLine.clear();
                        record.iterator().forEachRemaining(newLine::add);
                        dataSetResponse.add(newLine.toArray(new String[0]));
                    }
            );
        } catch (IOException ioe) {
            throw new DataAccessException("Failed to load data set from h2o with frame id: " + frameId, ioe);
        }

        return header;
    }

}
