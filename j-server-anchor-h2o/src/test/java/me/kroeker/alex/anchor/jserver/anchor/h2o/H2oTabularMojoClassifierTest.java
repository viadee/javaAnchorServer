package me.kroeker.alex.anchor.jserver.anchor.h2o;

import java.io.IOException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import de.viadee.anchorj.tabular.TabularInstance;
import hex.genmodel.easy.prediction.BinomialModelPrediction;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 *
 */
class H2oTabularMojoClassifierTest {

    @BeforeEach
    void setUp() {
    }

    @Test
    void testPredictAirline() throws IOException {
        H2oTabularMojoClassifier classifier = new H2oTabularMojoClassifier(
                this.getClass().getResourceAsStream("/" + Resources.AIRLINE_CLASSIFIER),
                (prediction) -> ((BinomialModelPrediction) prediction).labelIndex,
                Resources.AIRLINE_FEATURES
        );

        TabularInstance predictInstance = new TabularInstance(Resources.AIRLINE_FEATURE_MAPPING, null, Resources.SIMPLE_AIRLINE_INSTANCE);

        assertEquals(1, classifier.predict(predictInstance));
    }

}
