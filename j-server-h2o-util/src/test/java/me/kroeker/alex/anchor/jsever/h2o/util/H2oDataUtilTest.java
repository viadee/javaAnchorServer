package me.kroeker.alex.anchor.jsever.h2o.util;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.csv.CSVRecord;
import org.junit.jupiter.api.Test;
import me.kroeker.alex.anchor.jserver.h2o.util.H2oDataUtil;
import me.kroeker.alex.anchor.jserver.model.FeatureConditionEnum;
import me.kroeker.alex.anchor.jserver.model.FeatureConditionMetric;
import me.kroeker.alex.anchor.jserver.model.FeatureConditionsRequest;
import me.kroeker.alex.anchor.jserver.model.FrameInstance;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 *
 */
class H2oDataUtilTest {

    @Test
    void testGetRandomInstanceWithoutConditions() throws IOException {
        FeatureConditionsRequest request = new FeatureConditionsRequest();
        FrameInstance frame = H2oDataUtil.getRandomInstance(
                request,
                new File(this.getClass().getClassLoader().getResource(Resources.SIMPLE_CSV_FILE_STRING).getFile())
        );

        assertEquals(12, frame.getFeatureNamesMapping().size());
        assertEquals(12, frame.getInstance().length);
    }

    @Test
    void testGetRandomInstanceWithConditions() throws IOException {
        FeatureConditionsRequest request = new FeatureConditionsRequest();

        Map<String, FeatureConditionEnum> enumConditions = new HashMap<>(1);
        enumConditions.put("Sex", new FeatureConditionEnum("Sex", "female"));
        request.setEnumConditions(enumConditions);

        Map<String, FeatureConditionMetric> metricConditions = new HashMap<>(1);
        metricConditions.put("Pclass", new FeatureConditionMetric("Pclass", 2, 4));
        request.setMetricConditions(metricConditions);

        FrameInstance frame = H2oDataUtil.getRandomInstance(
                request,
                Resources.SIMPLE_CSV_FILE
        );

        assertEquals(12, frame.getFeatureNamesMapping().size());
        assertEquals("Heikkinen, Miss. Laina", frame.getInstance()[3]);
    }

    @Test
    void testIterateThroughCsvData() throws IOException {
        final Collection<CSVRecord> records = new ArrayList<>(4);
        H2oDataUtil.iterateThroughCsvData(Resources.SIMPLE_CSV_FILE, records::add);

        assertEquals(4, records.size());
    }

}
