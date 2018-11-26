package me.kroeker.alex.anchor.jserver.dao.h2o;

import me.kroeker.alex.anchor.jserver.h2o.util.H2oUtil;
import me.kroeker.alex.anchor.jserver.api.exceptions.DataAccessException;
import me.kroeker.alex.anchor.jserver.dao.ModelDAO;
import me.kroeker.alex.anchor.jserver.model.DataFrame;
import me.kroeker.alex.anchor.jserver.model.Model;
import org.springframework.stereotype.Component;
import water.bindings.pojos.ModelKeyV3;
import water.bindings.pojos.ModelSchemaBaseV3;
import water.bindings.pojos.ModelsV3;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

/**
 */
@Component
public class H2oModelDAO implements ModelDAO {

    @Override
    public Collection<Model> getModels(String connectionName) throws DataAccessException {
        try {
            ModelsV3 h2oModels = H2oUtil.createH2o(connectionName).models();
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
            ModelsV3 models = H2oUtil.createH2o(connectionName).model(modelKey);
            return transferToModelObject(models.models[0]);
        } catch (IOException ioe) {
            throw new DataAccessException("Failed to load model: " + modelId + "; from " + connectionName, ioe);
        }
    }

    private Model transferToModelObject(ModelSchemaBaseV3 h2oModel) {
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
        return model;
    }

}
