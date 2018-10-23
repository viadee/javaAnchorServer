package me.kroeker.alex.anchor.jserver.dao.h2o;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import me.kroeker.alex.anchor.jserver.dao.ConfigurationDAO;
import me.kroeker.alex.anchor.jserver.dao.exceptions.DataAccessException;
import me.kroeker.alex.anchor.jserver.model.DataFrame;
import me.kroeker.alex.anchor.jserver.model.Model;
import water.bindings.pojos.AboutV3;
import water.bindings.pojos.FrameBaseV3;
import water.bindings.pojos.FramesListV3;
import water.bindings.pojos.ModelSchemaBaseV3;
import water.bindings.pojos.ModelsV3;

/**
 * @author ak902764
 */
@Component
public class ConfigurationH2o extends BaseH2oAccess implements ConfigurationDAO {

    private static final Logger LOG = LoggerFactory.getLogger(ConfigurationH2o.class);

    @Override
    public boolean tryConnect(String connectionName) throws DataAccessException {
        try {
            AboutV3 about = this.createH2o(connectionName).about();
            return true;
        } catch (IOException | IllegalArgumentException ioe) {
            throw new DataAccessException("Failed to connect to h2o server with connection name: " +
                    connectionName, ioe);
        }
    }

    @Override
    public Collection<String> getConnectionNames() {
        return H2O_SERVER.keySet();
    }

    @Override
    public Collection<Model> getModels(String connectionName) throws DataAccessException {
        try {
            ModelsV3 h2oModels = this.createH2o(connectionName).models();
            Collection<Model> models = new ArrayList<>(h2oModels.models.length);

            for (ModelSchemaBaseV3 h2oModel : h2oModels.models) {
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

                models.add(model);
            }

            return models;
        } catch (IOException ioe) {
            throw new DataAccessException("Failed to retrieve models from " + connectionName + " due to: "
                    + ioe.getMessage(), ioe);
        }
    }

    @Override
    public Collection<DataFrame> getFrames(String connectionName) throws DataAccessException {
        try {
            FramesListV3 h2oFrames = this.createH2o(connectionName).frames();
            Collection<DataFrame> frames = new ArrayList<>(h2oFrames.frames.length);
            for (FrameBaseV3 h2oFrame : h2oFrames.frames) {
                DataFrame frame = new DataFrame();
                frame.setFrame_id(h2oFrame.frameId.name);
                frame.setName(h2oFrame.frameId.name);
                frame.setUrl(h2oFrame.frameId.url);

                frames.add(frame);
            }

            return frames;
        } catch (IOException ioe) {
            throw new DataAccessException("Failed to retrieve frames from " + connectionName + " due to: "
                    + ioe.getMessage(), ioe);
        }
    }
}
