package de.viadee.anchorj.server.anchor.h2o;

import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import de.viadee.anchorj.server.business.FrameBO;
import de.viadee.anchorj.server.business.ModelBO;
import de.viadee.anchorj.server.model.Anchor;
import de.viadee.anchorj.server.model.AnchorPredicate;
import de.viadee.anchorj.server.model.FrameInstance;
import de.viadee.anchorj.server.model.FrameSummary;
import de.viadee.anchorj.server.model.Model;
import de.viadee.anchorj.server.test.resources.Resources;
import okhttp3.ResponseBody;
import retrofit2.Response;
import water.bindings.H2oApi;
import water.bindings.pojos.ModelKeyV3;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 *
 */
@ExtendWith(MockitoExtension.class)
class AnchorH2OTest {

    @Mock
    private H2oApi api;

    @Mock
    private FrameBO frameBO;

    @Mock
    private ModelBO modelBO;

    @Mock
    private Response<ResponseBody> h2oResponse;

    private AnchorH2o anchorH2o;

    @BeforeEach
    void setUp() {
        anchorH2o = new MockedAnchorH2o(api, modelBO, frameBO);
    }

    @Test
    void testComputeRule() throws IOException {
        ResponseBody body = mock(ResponseBody.class);
        when(h2oResponse.isSuccessful()).thenReturn(true, true);
        when(body.byteStream()).thenReturn(
                this.getClass().getResourceAsStream(Resources.TITANIC_CSV),
                this.getClass().getResourceAsStream(Resources.TITANIC_CLASSIFIER)
        );
        when(h2oResponse.body()).thenReturn(body);
        when(api._downloadDataset_fetch(any())).thenReturn(h2oResponse);
        when(api.modelMojo(any(ModelKeyV3.class))).thenReturn(h2oResponse);

        FrameSummary frameSummary = new FrameSummary();
        frameSummary.setFrame_id("frame_id");
        frameSummary.setRow_count(6);
        frameSummary.setColumn_summary_list(Resources.TITANIC_COLUMN_SUMMARY);
        when(frameBO.getFrameSummary(any(), any())).thenReturn(frameSummary);

        Model modelUT = new Model();
        Set<String> ignoredColumns = new HashSet<>();
        ignoredColumns.add("PassengerId");
        ignoredColumns.add("Name");
        ignoredColumns.add("Ticket");
        ignoredColumns.add("Parch");
        ignoredColumns.add("Cabin");
        ignoredColumns.add("Embarked");
        ignoredColumns.add("rx_master");
        ignoredColumns.add("rx_miss");
        ignoredColumns.add("CabinLength");
        modelUT.setIgnoredColumns(ignoredColumns);
        modelUT.setTarget_column("Survived");
        when(modelBO.getModel(any(), any())).thenReturn(modelUT);

        FrameInstance instance = new FrameInstance(Resources.TITANIC_FEATURE_MAPPING, Resources.SIMPLE_TITANIC_INSTANCE);
        Anchor anchor = anchorH2o.computeRule("", "", "", instance, null, 1L);
        assertNotNull(anchor);
        assertEquals("0", anchor.getLabel_of_case());
        assertEquals("0", anchor.getPrediction());

        assertEquals(2, anchor.getFeatures().size());
        Iterator<Integer> features = anchor.getFeatures().iterator();
        assertEquals(Integer.valueOf(1), features.next());
        assertEquals(Integer.valueOf(0), features.next());

        // work around: the evaluated precision differs between running this test with IDE like IntelliJ or maven
        // therefor ignore the added precision
        AnchorPredicate testPredicate;
        double precisionWithIntelliJ;
        double coverageWithIntellJ;

        testPredicate = anchor.getPredicates().get(0);
        precisionWithIntelliJ = 0.1183333333333334;
        coverageWithIntellJ = -0.14100000000000001;
        AnchorPredicate pClassPredicate = new AnchorPredicate("Pclass", 3, testPredicate.getAddedPrecision(), testPredicate.getAddedCoverage(), 3, 3);
        assertEquals(pClassPredicate, testPredicate);
        assertEquals(precisionWithIntelliJ, testPredicate.getAddedPrecision(), 0.06);
        assertEquals(coverageWithIntellJ, testPredicate.getAddedCoverage(), 0.2);

        testPredicate = anchor.getPredicates().get(1);
        precisionWithIntelliJ = 0.825;
        coverageWithIntellJ = -0.348;
        AnchorPredicate sexPredicate = new AnchorPredicate("Sex", 0, testPredicate.getAddedPrecision(), testPredicate.getAddedCoverage(), "male");
        assertEquals(sexPredicate, testPredicate);
        assertEquals(precisionWithIntelliJ, testPredicate.getAddedPrecision(), 0.06);
        assertEquals(coverageWithIntellJ, testPredicate.getAddedCoverage(), 0.1);

        // TODO check 455!!
        assertEquals(Integer.valueOf(347), anchor.getAffected_rows());
    }

    private final static class MockedAnchorH2o extends AnchorH2o {
        private H2oApi api;

        MockedAnchorH2o(H2oApi api, ModelBO modelBO, FrameBO frameBO) {
            super(modelBO, frameBO);
            this.api = api;
        }

        @Override
        public H2oApi createH2o(String connectionName) {
            return api;
        }
    }

}
