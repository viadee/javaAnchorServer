package de.viadee.anchorj.server.dao.h2o;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import de.viadee.anchorj.server.api.exceptions.DataAccessException;
import de.viadee.anchorj.server.configuration.AppConfiguration;
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
import water.bindings.pojos.StackedEnsembleModelV99;

/**
 *
 */
@Component
public class H2oModelDAO implements ModelDAO, H2oConnector {

    private AppConfiguration configuration;

    public H2oModelDAO(@Autowired AppConfiguration configuration) {
        this.configuration = configuration;
    }

    @Override
    public Collection<Model> getModels(String connectionName) throws DataAccessException {
        try {
            ModelsV3 h2oModels = this.createH2o(this.configuration, connectionName).models();
            Collection<Model> models = new ArrayList<>(h2oModels.models.length);

            for (ModelSchemaBaseV3 h2oModel : h2oModels.models) {
                models.add(this.getModel(connectionName, h2oModel.modelId.name));
            }

            return models;
        } catch (IOException ioe) {
            throw new DataAccessException("Failed to retrieve models from " + connectionName + " due to: "
                    + ioe.getMessage(), ioe);
        }
    }

    @Override
    public Model getModel(String connectionName, String modelId) throws DataAccessException {
        ModelsV3 modelsV3 = new ModelsV3();
        modelsV3.findCompatibleFrames = true;
        modelsV3.modelId = new ModelKeyV3();
        modelsV3.modelId.name = modelId;

        try {
            ModelsV3 models = this.createH2o(this.configuration, connectionName).model(modelsV3);
            return transferToModelObject(models.models[0]);
        } catch (IOException ioe) {
            throw new DataAccessException("Failed to load model: " + modelId + "; from " + connectionName, ioe);
        }
    }

    private Model transferToModelObject(ModelSchemaBaseV3 h2oModel) {
        final String algoName = h2oModel.algo;
        Model model = new Model();

        model.setModel_id(h2oModel.modelId.name);
        model.setName(h2oModel.modelId.name);
        model.setTarget_column(h2oModel.responseColumnName);
        model.setUrl(h2oModel.modelId.url);

        DataFrame frame = new DataFrame();
        frame.setFrame_id(h2oModel.dataFrame.name);
        frame.setName(h2oModel.dataFrame.name);
        frame.setUrl(h2oModel.dataFrame.url);
        model.setData_frame(frame);

        extractModelSpecificValues(model, h2oModel, algoName);

        return model;
    }

    private void extractModelSpecificValues(Model model, ModelSchemaBaseV3 h2oModel, String algoName) {
        String[] ignoredColumns;
        String[] compatibleFrames;
        switch (algoName) {
            case "gbm":
                GBMModelV3 gbmModel = (GBMModelV3) h2oModel;
                ignoredColumns = gbmModel.parameters != null ? gbmModel.parameters.ignoredColumns : null;
                compatibleFrames = gbmModel.compatibleFrames;
                break;
            case "drf":
            case "random_forest":
                DRFModelV3 drfModel = (DRFModelV3) h2oModel;
                ignoredColumns = drfModel.parameters != null ? drfModel.parameters.ignoredColumns : null;
                compatibleFrames = drfModel.compatibleFrames;
                break;
            case "deeplearning":
                DeepLearningModelV3 dlModel = (DeepLearningModelV3) h2oModel;
                ignoredColumns = dlModel.parameters != null ? dlModel.parameters.ignoredColumns : null;
                compatibleFrames = dlModel.compatibleFrames;
                break;
            case "glm":
                GLMModelV3 glmModel = (GLMModelV3) h2oModel;
                ignoredColumns = glmModel.parameters != null ? glmModel.parameters.ignoredColumns : null;
                compatibleFrames = glmModel.compatibleFrames;
                break;
            case "naive_bayes":
                NaiveBayesModelV3 nbModel = (NaiveBayesModelV3) h2oModel;
                ignoredColumns = nbModel.parameters != null ? nbModel.parameters.ignoredColumns : null;
                compatibleFrames = nbModel.compatibleFrames;
                break;
            case "kmeans":
                KMeansModelV3 kmModel = (KMeansModelV3) h2oModel;
                ignoredColumns = kmModel.parameters != null ? kmModel.parameters.ignoredColumns : null;
                compatibleFrames = kmModel.compatibleFrames;
                break;
            case "pca":
                PCAModelV3 pcaModel = (PCAModelV3) h2oModel;
                ignoredColumns = pcaModel.parameters != null ? pcaModel.parameters.ignoredColumns : null;
                compatibleFrames = pcaModel.compatibleFrames;
                break;
            case "stackedensemble":
                StackedEnsembleModelV99 seModel = (StackedEnsembleModelV99) h2oModel;
                ignoredColumns = seModel.parameters != null ? seModel.parameters.ignoredColumns : null;
                compatibleFrames = seModel.compatibleFrames;
                break;
            default:
                throw new IllegalArgumentException("Model with algo " + algoName + " not handled");
        }

        if (ignoredColumns != null) {
            model.setIgnoredColumns(new HashSet<>(Arrays.asList(ignoredColumns)));
        } else {
            model.setIgnoredColumns(Collections.emptySet());
        }

        if (compatibleFrames != null) {
            model.setCompatibleFrames(Arrays.asList(compatibleFrames));
        } else {
            model.setCompatibleFrames(Collections.emptySet());
        }
    }

}
