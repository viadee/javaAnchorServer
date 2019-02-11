package de.viadee.anchorj.server.anchor.h2o.spark;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import de.viadee.anchorj.server.business.FrameBO;
import de.viadee.anchorj.server.business.ModelBO;
import de.viadee.anchorj.server.configuration.AppConfiguration;
import de.viadee.anchorj.server.model.FrameInstance;
import de.viadee.anchorj.server.model.FrameSummary;
import de.viadee.anchorj.server.model.Model;
import de.viadee.anchorj.server.test.resources.Resources;
import okhttp3.ResponseBody;
import retrofit2.Response;
import water.bindings.H2oApi;
import water.bindings.pojos.ModelKeyV3;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.lessThan;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 *
 */
@ExtendWith(MockitoExtension.class)
class AnchorSparkIT {

    @Mock
    private H2oApi api;

    @Mock
    private FrameBO frameBO;

    @Mock
    private ModelBO modelBO;

    private AnchorSpark anchorSpark;

    @BeforeEach
    void setUp() {
        anchorSpark = new MockedAnchorSpark(api, modelBO, frameBO);
    }

    @SuppressWarnings("Duplicates")
    private FrameInstance prepareAnchorRun() throws IOException {
        //noinspection unchecked
        Response<ResponseBody> h2oResponse = mock(Response.class);
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

        return new FrameInstance(Resources.TITANIC_FEATURE_MAPPING, Resources.SIMPLE_TITANIC_INSTANCE);
    }

    @Test
    void testSparkGlobalExplanation() throws IOException {
        FrameInstance instance = this.prepareAnchorRun();
        anchorSpark.runSubmodularPick("", "", "", instance, null);
        assertThat(Thread.activeCount(), lessThan(200));
    }

    private final static class MockedAnchorSpark extends AnchorSpark {
        private H2oApi api;

        private MockedAnchorSpark(H2oApi api, ModelBO modelBO, FrameBO frameBO) {
            super(modelBO, frameBO, new SparkConfiguration(new MyAppConfiguration()), new MyAppConfiguration());
            this.api = api;
        }

        @Override
        public H2oApi createH2o(AppConfiguration configuration, String connectionName) {
            return api;
        }

    }

    private static class MyAppConfiguration implements AppConfiguration {
        @Override
        public Set<String> getConnectionNames() {
            return null;
        }

        @Override
        public String getConnectionName(String connectionName) {
            return null;
        }

        @Override
        public String getSparkLibFolder() {
            return new File(new File("").getAbsolutePath(), "target/libs").getAbsolutePath();
        }

        @Override
        public String getSparkMasterUrl() {
            return "spark://localhost:7077";
        }
    }

}
