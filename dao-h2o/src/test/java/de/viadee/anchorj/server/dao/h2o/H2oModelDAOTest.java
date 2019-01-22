package de.viadee.anchorj.server.dao.h2o;

import java.io.IOException;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import de.viadee.anchorj.server.model.Model;
import water.bindings.H2oApi;
import water.bindings.pojos.DRFModelV3;
import water.bindings.pojos.DRFParametersV3;
import water.bindings.pojos.DeepLearningModelV3;
import water.bindings.pojos.DeepLearningParametersV3;
import water.bindings.pojos.FrameKeyV3;
import water.bindings.pojos.GBMModelV3;
import water.bindings.pojos.GBMParametersV3;
import water.bindings.pojos.ModelKeyV3;
import water.bindings.pojos.ModelSchemaBaseV3;
import water.bindings.pojos.ModelsV3;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

/**
 *
 */
@ExtendWith(MockitoExtension.class)
class H2oModelDAOTest {

    @Mock
    private H2oApi api;

    @Test
    void testGetGbmModel() throws IOException {
        H2oModelDAO modelDAO = new MockModelDAO();
        ModelsV3 modelResponse = new ModelsV3();
        when(api.model(any(ModelKeyV3.class))).thenReturn(modelResponse);
        GBMModelV3 h2oModel = new GBMModelV3();
        h2oModel.parameters = new GBMParametersV3();
        h2oModel.parameters.ignoredColumns = new String[]{"A"};
        modelResponse.models = new ModelSchemaBaseV3[]{h2oModel};

        ModelKeyV3 modelKey = new ModelKeyV3();
        h2oModel.algo = "gbm";
        modelKey.name = "modelId";
        modelKey.url = "url";
        h2oModel.modelId = modelKey;
        h2oModel.responseColumnName = "A";

        FrameKeyV3 h2oFrame = new FrameKeyV3();
        h2oFrame.name = "frame";
        h2oFrame.url = "frameUrl";
        h2oModel.dataFrame = h2oFrame;

        Model model = modelDAO.getModel("", "");
        assertEquals("modelId", model.getModel_id());
        assertEquals("modelId", model.getName());
        assertEquals("url", model.getUrl());
        assertEquals("A", model.getTarget_column());
        assertEquals("frame", model.getData_frame().getFrame_id());
        assertEquals("frame", model.getData_frame().getName());
        assertEquals("frameUrl", model.getData_frame().getUrl());
        assertEquals(1, model.getIgnoredColumns().size());
        assertEquals("A", model.getIgnoredColumns().iterator().next());
    }

    @Test
    void testGetDrfModel() throws IOException {
        H2oModelDAO modelDAO = new MockModelDAO();
        ModelsV3 modelResponse = new ModelsV3();
        when(api.model(any(ModelKeyV3.class))).thenReturn(modelResponse);
        DRFModelV3 h2oModel = new DRFModelV3();
        h2oModel.parameters = new DRFParametersV3();
        h2oModel.parameters.ignoredColumns = new String[]{"A"};
        modelResponse.models = new ModelSchemaBaseV3[]{h2oModel};

        ModelKeyV3 modelKey = new ModelKeyV3();
        h2oModel.algo = "drf";
        modelKey.name = "modelId";
        modelKey.url = "url";
        h2oModel.modelId = modelKey;
        h2oModel.responseColumnName = "A";

        FrameKeyV3 h2oFrame = new FrameKeyV3();
        h2oFrame.name = "frame";
        h2oFrame.url = "frameUrl";
        h2oModel.dataFrame = h2oFrame;

        Model model = modelDAO.getModel("", "");
        assertEquals("modelId", model.getModel_id());
        assertEquals("modelId", model.getName());
        assertEquals("url", model.getUrl());
        assertEquals("A", model.getTarget_column());
        assertEquals("frame", model.getData_frame().getFrame_id());
        assertEquals("frame", model.getData_frame().getName());
        assertEquals("frameUrl", model.getData_frame().getUrl());
        assertEquals(1, model.getIgnoredColumns().size());
        assertEquals("A", model.getIgnoredColumns().iterator().next());
    }

    @Test
    void testGetDLModel() throws IOException {
        H2oModelDAO modelDAO = new MockModelDAO();
        ModelsV3 modelResponse = new ModelsV3();
        when(api.model(any(ModelKeyV3.class))).thenReturn(modelResponse);
        DeepLearningModelV3 h2oModel = new DeepLearningModelV3();
        h2oModel.parameters = new DeepLearningParametersV3();
        h2oModel.parameters.ignoredColumns = new String[]{"A"};
        modelResponse.models = new ModelSchemaBaseV3[]{h2oModel};

        ModelKeyV3 modelKey = new ModelKeyV3();
        h2oModel.algo = "deeplearning";
        modelKey.name = "modelId";
        modelKey.url = "url";
        h2oModel.modelId = modelKey;
        h2oModel.responseColumnName = "A";

        FrameKeyV3 h2oFrame = new FrameKeyV3();
        h2oFrame.name = "frame";
        h2oFrame.url = "frameUrl";
        h2oModel.dataFrame = h2oFrame;

        Model model = modelDAO.getModel("", "");
        assertEquals("modelId", model.getModel_id());
        assertEquals("modelId", model.getName());
        assertEquals("url", model.getUrl());
        assertEquals("A", model.getTarget_column());
        assertEquals("frame", model.getData_frame().getFrame_id());
        assertEquals("frame", model.getData_frame().getName());
        assertEquals("frameUrl", model.getData_frame().getUrl());
        assertEquals(1, model.getIgnoredColumns().size());
        assertEquals("A", model.getIgnoredColumns().iterator().next());
    }

    private class MockModelDAO extends H2oModelDAO {
        @Override
        public H2oApi createH2o(String connectionName) {
            return api;
        }
    }
}
