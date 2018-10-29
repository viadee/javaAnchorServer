package me.kroeker.alex.anchor.jserver.dao.h2o;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Function;

import org.apache.commons.csv.CSVRecord;
import org.springframework.stereotype.Component;
import me.kroeker.alex.anchor.jserver.dao.RuleDAO;
import me.kroeker.alex.anchor.jserver.dao.exceptions.DataAccessException;
import me.kroeker.alex.anchor.jserver.dao.h2o.util.H2oUtil;
import me.kroeker.alex.anchor.jserver.model.CaseSelectConditionRequest;
import me.kroeker.alex.anchor.jserver.model.Rule;
import water.bindings.H2oApi;

/**
 * @author ak902764
 */
@Component
public class RuleH2o extends BaseH2oAccess implements RuleDAO {

    @Override
    public Rule randomRule(
            String connectionName,
            String modelId,
            String frameId,
            CaseSelectConditionRequest conditions
    ) throws DataAccessException {

        H2oApi api = this.createH2o(connectionName);
        try {
            File dataSet = H2oUtil.downloadDataSet(H2oApi.stringToFrameKey(frameId), api);

            CSVRecord instanceToExplain = getRandomInstance(conditions, dataSet);

            return null;
        } catch (IOException ioe) {
            throw new DataAccessException("Failed to retrieve frame summary of h2o with connection name: "
                    + connectionName + " and frame id: " + frameId, ioe);
        }
    }

    private CSVRecord getRandomInstance(CaseSelectConditionRequest conditions, File dataSet) throws IOException {
        Collection<Function<CSVRecord, Boolean>> filters = new ArrayList<>();
        if (conditions.getEnumConditions() != null) {
            conditions.getEnumConditions().values().forEach(
                    (entry) ->
                            filters.add(
                                    (record) ->
                                            record.get(entry.getFeatureName()).equals(entry.getCategory())
                            )
            );
        }
        // TODO handle metrics min max problem. wenn das maximum 20 ist und 20 als condition, sollte 20 inklusive sein
        if (conditions.getMetricConditions() != null) {
            conditions.getMetricConditions().values().forEach(
                    (entry) ->
                            filters.add(
                                    (record) -> {
                                        double recordValue = Double.parseDouble(record.get(entry.getFeatureName()));
                                        return entry.getConditionMin() < recordValue && recordValue < entry.getConditionMax();
                                    }
                            )
            );
        }

        List<CSVRecord> acceptedRecords = new ArrayList<>();
        H2oUtil.iterateThroughCsvData(dataSet,
                (record) -> {
                    if (filters.stream().allMatch(filter -> filter.apply(record))) {
                        acceptedRecords.add(record);
                    }
                }
        );

        int randomNum = ThreadLocalRandom.current().nextInt(0, acceptedRecords.size());
        return acceptedRecords.get(randomNum);
    }

}
