package me.kroeker.alex.anchor.jserver.anchor.h2o;

import java.io.IOException;
import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;
import de.viadee.anchorj.tabular.TabularInstance;
import hex.genmodel.easy.prediction.BinomialModelPrediction;

import static org.junit.Assert.assertEquals;

/**
 *
 */
public class H2oTabularMojoClassifierTest {

    private Object[] instance;

    @Before
    public void setUp() {
        instance = new Object[31];
        instance[0] = 1987;
        instance[1] = 10;
        instance[2] = 14;
        instance[3] = 3;
        instance[4] = 741;
        instance[5] = 730;
        instance[6] = 912;
        instance[7] = 849;
        instance[8] = "PS";
        instance[9] = 1451;
        instance[10] = "NA";
        instance[11] = 91;
        instance[12] = 79;
        instance[13] = null;
        instance[14] = 23;
        instance[15] = 11;
        instance[16] = "SAN";
        instance[17] = "SFO";
        instance[18] = 447;
        instance[19] = null;
        instance[20] = null;
        instance[21] = 0;
        instance[22] = "NA";
        instance[23] = 0;
        instance[24] = null;
        instance[25] = null;
        instance[26] = null;
        instance[27] = null;
        instance[28] = null;
        instance[29] = "YES";
        instance[30] = "YES";
    }

    @Test
    public void testPredictAirline() throws IOException {
        H2oTabularMojoClassifier classifier = new H2oTabularMojoClassifier(
                this.getClass().getResourceAsStream("/" + Resources.AIRLINE_CLASSIFIER),
                (prediction) -> ((BinomialModelPrediction) prediction).labelIndex,
                Resources.AIRLINE_FEATURES,
                Arrays.asList("Origin")
        );

        TabularInstance predictInstance = new TabularInstance(Resources.AIRLINE_FEATURE_MAPPING, null, instance);

        assertEquals(1, classifier.predict(predictInstance));
    }

}
