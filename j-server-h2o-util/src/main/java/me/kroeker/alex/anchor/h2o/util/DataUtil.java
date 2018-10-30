package me.kroeker.alex.anchor.h2o.util;

import de.goerke.tobias.anchorj.tabular.TabularInstance;
import me.kroeker.alex.anchor.jserver.model.CaseSelectConditionEnum;
import me.kroeker.alex.anchor.jserver.model.CaseSelectConditionMetric;
import me.kroeker.alex.anchor.jserver.model.CaseSelectConditionRequest;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Consumer;
import java.util.function.Function;

public final class DataUtil {

    private DataUtil() {
    }

    public static TabularInstance getRandomInstance(CaseSelectConditionRequest conditions, File dataSet) throws IOException {
        Collection<Function<CSVRecord, Boolean>> filters = new ArrayList<>();
        if (conditions.getEnumConditions() != null) {
            for (CaseSelectConditionEnum enumCondition : conditions.getEnumConditions().values()) {
                filters.add(
                        (record) ->
                                record.get(enumCondition.getFeatureName()).equals(enumCondition.getCategory())
                );
            }
        }
        // TODO handle metrics min max problem. wenn das maximum 20 ist und 20 als condition, sollte 20 inklusive sein
        if (conditions.getMetricConditions() != null) {
            for (CaseSelectConditionMetric metricCondition : conditions.getMetricConditions().values()) {
                filters.add(
                        (record) -> {
                            double recordValue = Double.parseDouble(record.get(metricCondition.getFeatureName()));
                            return metricCondition.getConditionMin() < recordValue && recordValue < metricCondition.getConditionMax();
                        }
                );
            }
        }

        List<CSVRecord> acceptedRecords = new ArrayList<>();
        DataUtil.iterateThroughCsvData(dataSet,
                (record) -> {
                    if (filters.stream().allMatch(filter -> filter.apply(record))) {
                        acceptedRecords.add(record);
                    }
                }
        );

        int randomNum = ThreadLocalRandom.current().nextInt(0, acceptedRecords.size());
        CSVRecord acceptedInstance = acceptedRecords.get(randomNum);

        // TODO refactor csvrecord to tabular instance
        return new TabularInstance(acceptedInstance.toMap().values().toArray());
    }

    public static void iterateThroughCsvData(File file, Consumer<CSVRecord> recordConsumer) throws IOException {
        try (Reader in = new FileReader(file)) {
            Iterable<CSVRecord> records = CSVFormat.DEFAULT.withFirstRecordAsHeader().parse(in);
            for (CSVRecord record : records) {
                recordConsumer.accept(record);
            }
        }
    }

}
