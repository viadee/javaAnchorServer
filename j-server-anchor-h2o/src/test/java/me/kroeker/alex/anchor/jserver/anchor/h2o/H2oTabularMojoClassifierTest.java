package me.kroeker.alex.anchor.jserver.anchor.h2o;

import java.io.IOException;
import java.util.Arrays;

import org.junit.jupiter.api.Test;
import de.viadee.anchorj.tabular.TabularInstance;
import hex.genmodel.easy.prediction.RegressionModelPrediction;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author ak902764
 */
public class H2oTabularMojoClassifierTest {

    @Test
    public void test() throws IOException {
        H2oTabularMojoClassifier classifier = new H2oTabularMojoClassifier(
                this.getClass().getResourceAsStream("/" + Resources.AIRLINE_CLASSIFIER),
                (prediction) -> (int) ((RegressionModelPrediction) prediction).value,
                Resources.AIRLINE_FEATURES,
                Arrays.asList("Origin")
        );

        Object[] instance = new Object[31];
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
        instance[14] = null;
        instance[15] = 23;
        instance[16] = 11;
        instance[17] = "SAN";
        instance[18] = "SFO";
        instance[19] = 447;
        instance[20] = null;
        instance[21] = null;
        instance[22] = 0;
        instance[23] = "NA";
        instance[24] = 0;
        instance[25] = null;
        instance[26] = null;
        instance[27] = null;
        instance[28] = null;
        instance[29] = null;
        instance[30] = "YES";
        TabularInstance predictInstance = new TabularInstance(Resources.AIRLINE_FEATURE_MAPPING, null, instance);

        assertEquals(0, classifier.predict(predictInstance));
    }

}
