package me.kroeker.alex.anchor.jsever.anchor.h2o;

import de.goerke.tobias.anchorj.tabular.AnchorTabular;
import de.goerke.tobias.anchorj.tabular.PercentileDiscretizer;
import de.goerke.tobias.anchorj.tabular.TabularInstance;
import me.kroeker.alex.anchor.h2o.util.H2oDataDownload;
import me.kroeker.alex.anchor.h2o.util.H2oDataUtil;
import me.kroeker.alex.anchor.h2o.util.H2oUtil;
import me.kroeker.alex.anchor.jserver.anchor.AnchorRule;
import me.kroeker.alex.anchor.jserver.api.exceptions.DataAccessException;
import me.kroeker.alex.anchor.jserver.business.ConfigurationBO;
import me.kroeker.alex.anchor.jserver.business.DataBO;
import me.kroeker.alex.anchor.jserver.model.FrameSummary;
import me.kroeker.alex.anchor.jserver.model.Model;
import me.kroeker.alex.anchor.jserver.model.Rule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import water.bindings.H2oApi;
import water.bindings.pojos.ModelExportV3;
import water.bindings.pojos.ModelKeyV3;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

@Component
public class AnchorRuleH2o implements AnchorRule {

    @Autowired
    private DataBO dataBO;

    @Autowired
    private ConfigurationBO configurationBO;

    @Override
    public Rule computeRule(String connectionName, String modelId, String frameId, TabularInstance instance) throws DataAccessException {
        AnchorTabular anchor = buildAnchor(connectionName, modelId, frameId);

        ModelKeyV3 modelKey = new ModelKeyV3();
        modelKey.name = modelId;
        try {
            ModelExportV3 mojo = H2oUtil.createH2o(connectionName).exportMojo(modelKey);
            System.out.println(mojo._excludeFields);
        } catch (IOException e) {
            throw new DataAccessException("Failed to load Model MOJO with id: " + modelId + " and connection: " + connectionName);
        }
//        final ClassificationFunction<TabularInstance> classificationFunction = new H2OTabularRegressionMojoClassifier<>(
//                FileReader.readResource("duni/gbm_grid_model_94.zip"),
//                anchorTabular.getFeatures().stream().map(TabularFeature::getName).collect(Collectors.toList()));


        return null;
    }

    private AnchorTabular buildAnchor(String connectionName, String modelId, String frameId) throws DataAccessException {
        H2oApi api = H2oUtil.createH2o(connectionName);
        Collection<String[]> anchorData = new ArrayList<>();
        Model model = configurationBO.getModel(connectionName, modelId);

        try (H2oDataDownload h2oDownload = new H2oDataDownload()) {
            File dataSet = h2oDownload.getFile(api, H2oApi.stringToFrameKey(frameId));
            H2oDataUtil.iterateThroughCsvData(dataSet, (record) ->
                    anchorData.add(record.toMap().values().toArray(new String[0]))
            );
        } catch (IOException ioe) {
            throw new DataAccessException("Failed to retrieve frame summary of h2o with connection name: "
                    + connectionName + " and frame id: " + frameId, ioe);
        }

        AnchorTabular.TabularPreprocessorBuilder anchorBuilder = new AnchorTabular.TabularPreprocessorBuilder(false, anchorData);
        final PercentileDiscretizer percentileDiscretizer = new PercentileDiscretizer(5);
        FrameSummary frameSummary = this.dataBO.getFrame(connectionName, frameId);
        frameSummary.getColumn_summary_list().forEach((column) -> {
            final String columnLabel = column.getLabel();
            if (columnLabel.equals(model.getTarget_column())) {
                anchorBuilder.addTargetColumn(columnLabel);
            } else if (H2oUtil.isStringColumn(column.getColumn_type()) || H2oUtil.isEnumColumn(column.getColumn_type())) {
                anchorBuilder.addObjectColumn(columnLabel);
            } else {
                anchorBuilder.addNominalColumn(columnLabel, percentileDiscretizer);
            }
        });

        return anchorBuilder.build();
    }

}
