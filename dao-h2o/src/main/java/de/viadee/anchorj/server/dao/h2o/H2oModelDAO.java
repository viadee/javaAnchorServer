package de.viadee.anchorj.server.dao.h2o;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;

import org.springframework.stereotype.Component;
import de.viadee.anchorj.server.api.exceptions.DataAccessException;
import de.viadee.anchorj.server.dao.ModelDAO;
import de.viadee.anchorj.server.h2o.util.H2oConnector;
import de.viadee.anchorj.server.model.DataFrame;
import de.viadee.anchorj.server.model.Model;
import water.bindings.pojos.DRFModelV3;
import water.bindings.pojos.DeepLearningModelV3;
import water.bindings.pojos.GBMModelV3;
import water.bindings.pojos.GLMModelV3;
import water.bindings.pojos.KMeansModelV3;
import water.bindings.pojos.ModelKeyV3;
import water.bindings.pojos.ModelSchemaBaseV3;
import water.bindings.pojos.ModelsV3;
import water.bindings.pojos.NaiveBayesModelV3;
import water.bindings.pojos.PCAModelV3;

/**
 *
 */
@Component
public class H2oModelDAO implements ModelDAO, H2oConnector {

    @Override
    public Collection<Model> getModels(String connectionName) throws DataAccessException {
        try {
            ModelsV3 h2oModels = this.createH2o(connectionName).models();
            Collection<Model> models = new ArrayList<>(h2oModels.models.length);

            for (ModelSchemaBaseV3 h2oModel : h2oModels.models) {
                Model model = transferToModelObject(h2oModel);

                models.add(model);
            }

            return models;
        } catch (IOException ioe) {
            throw new DataAccessException("Failed to retrieve models from " + connectionName + " due to: "
                    + ioe.getMessage(), ioe);
        }
    }

    @Override
    public Model getModel(String connectionName, String modelId) throws DataAccessException {
        ModelKeyV3 modelKey = new ModelKeyV3();
        modelKey.name = modelId;

        try {
            ModelsV3 models = this.createH2o(connectionName).model(modelKey);
            return transferToModelObject(models.models[0]);
        } catch (IOException ioe) {
            throw new DataAccessException("Failed to load model: " + modelId + "; from " + connectionName, ioe);
        }
    }

    private Model transferToModelObject(ModelSchemaBaseV3 h2oModel) {
        String[] ignoredColumns;
        final String algoName = h2oModel.algo;
        switch (algoName) {
            case "gbm":
                GBMModelV3 gbmModel = (GBMModelV3) h2oModel;
                ignoredColumns = gbmModel.parameters != null ? gbmModel.parameters.ignoredColumns : null;
                break;
            case "drf":
            case "random_forest":
                DRFModelV3 drfModel = (DRFModelV3) h2oModel;
                ignoredColumns = drfModel.parameters != null ? drfModel.parameters.ignoredColumns : null;
                break;
            case "deeplearning":
                DeepLearningModelV3 dlModel = (DeepLearningModelV3) h2oModel;
                ignoredColumns = dlModel.parameters != null ? dlModel.parameters.ignoredColumns : null;
                break;
            case "glm":
                GLMModelV3 glmModel = (GLMModelV3) h2oModel;
                ignoredColumns = glmModel.parameters != null ? glmModel.parameters.ignoredColumns : null;
                break;
            case "naive_bayes":
                NaiveBayesModelV3 nbModel = (NaiveBayesModelV3) h2oModel;
                ignoredColumns = nbModel.parameters != null ? nbModel.parameters.ignoredColumns : null;
                break;
            case "kmeans":
                KMeansModelV3 kmModel = (KMeansModelV3) h2oModel;
                ignoredColumns = kmModel.parameters != null ? kmModel.parameters.ignoredColumns : null;
                break;
            case "pca":
                PCAModelV3 pcaModel = (PCAModelV3) h2oModel;
                ignoredColumns = pcaModel.parameters != null ? pcaModel.parameters.ignoredColumns : null;
                break;
            default:
                throw new IllegalArgumentException("Model with algo " + algoName + " not handled");
        }

        Model model = new Model();
        if (ignoredColumns != null) {
            model.setIgnoredColumns(new HashSet<>(Arrays.asList(ignoredColumns)));
        }
        model.setModel_id(h2oModel.modelId.name);
        model.setName(h2oModel.modelId.name);
        model.setTarget_column(h2oModel.responseColumnName);
        model.setUrl(h2oModel.modelId.url);

        DataFrame frame = new DataFrame();
        frame.setFrame_id(h2oModel.dataFrame.name);
        frame.setName(h2oModel.dataFrame.name);
        frame.setUrl(h2oModel.dataFrame.url);
        model.setData_frame(frame);
        return model;
    }

}
