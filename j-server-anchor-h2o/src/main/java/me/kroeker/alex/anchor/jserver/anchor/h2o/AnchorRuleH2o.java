package me.kroeker.alex.anchor.jserver.anchor.h2o;

import de.goerke.tobias.anchorj.base.AnchorConstructionBuilder;
import de.goerke.tobias.anchorj.base.AnchorResult;
import de.goerke.tobias.anchorj.base.ClassificationFunction;
import de.goerke.tobias.anchorj.base.exploration.BatchSAR;
import de.goerke.tobias.anchorj.tabular.*;
import me.kroeker.alex.anchor.h2o.util.*;
import me.kroeker.alex.anchor.jserver.anchor.AnchorRule;
import me.kroeker.alex.anchor.jserver.api.exceptions.DataAccessException;
import me.kroeker.alex.anchor.jserver.business.ConfigurationBO;
import me.kroeker.alex.anchor.jserver.business.DataBO;
import me.kroeker.alex.anchor.jserver.model.ColumnSummary;
import me.kroeker.alex.anchor.jserver.model.FrameSummary;
import me.kroeker.alex.anchor.jserver.model.Model;
import me.kroeker.alex.anchor.jserver.model.Rule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import water.bindings.H2oApi;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class AnchorRuleH2o implements AnchorRule {

    private DataBO dataBO;

    private ConfigurationBO configurationBO;

    public AnchorRuleH2o(@Autowired DataBO dataBO, @Autowired ConfigurationBO configurationBO) {
        this.dataBO = dataBO;
        this.configurationBO = configurationBO;
    }

    @Override
    public Rule computeRule(String connectionName, String modelId, String frameId, TabularInstance instance) throws DataAccessException {
        AnchorTabular anchor = buildAnchor(connectionName, modelId, frameId);

        // TODO fix
        Object[] instance2 = instance.getInstance();
        Object[] instanceWithoutTarget = new Object[instance2.length - 1];
        for (int i = 0; i < instance2.length - 1; i++) {
            instanceWithoutTarget[i] = instance2[i];
        }
        instance = new TabularInstance(instanceWithoutTarget);



        try (H2oDownload mojoDownload = new H2oMojoDownload()) {
            File mojoFile = mojoDownload.getFile(H2oUtil.createH2o(connectionName), modelId);

            final ClassificationFunction<TabularInstance> classificationFunction = new H2OTabularMojoClassifier<>(
                    new FileInputStream(mojoFile),
                    anchor.getFeatures().stream().map(TabularFeature::getName).collect(Collectors.toList()));

            TabularPerturbationFunction tabularPerturbationFunction = new TabularPerturbationFunction(instance,
                    anchor.getTabularInstances().toArray(new TabularInstance[0]));

            final AnchorResult<TabularInstance> anchorResult = new AnchorConstructionBuilder<>(classificationFunction,
                    tabularPerturbationFunction, instance,
                    classificationFunction.predict(instance))
                    .enableThreading(10, true)
                    .setBestAnchorIdentification(new BatchSAR(20, 20))
                    .setInitSampleCount(200)
                    .setTau(0.8)
                    .build().constructAnchor();

            return null;

        } catch (IOException e) {
            throw new DataAccessException("Failed to load Model MOJO with id: " + modelId + " and connection: " + connectionName);
        }
    }

    private AnchorTabular buildAnchor(String connectionName, String modelId, String frameId) throws DataAccessException {
        H2oApi api = H2oUtil.createH2o(connectionName);
        Collection<String[]> anchorData = new ArrayList<>();
        Model model = configurationBO.getModel(connectionName, modelId);

        Map<String, Integer> header;
        try (H2oFrameDownload h2oDownload = new H2oFrameDownload()) {
            File dataSetFile = h2oDownload.getFile(api, frameId);
            Collection<String> newLine = new ArrayList<>();
            header = H2oDataUtil.iterateThroughCsvData(dataSetFile, (record) -> {
                        newLine.clear();
                        record.iterator().forEachRemaining(newLine::add);
                        anchorData.add(newLine.toArray(new String[0]));
                    }
            );
        } catch (IOException ioe) {
            throw new DataAccessException("Failed to retrieve frame summary of h2o with connection name: "
                    + connectionName + " and frame id: " + frameId, ioe);
        }

        AnchorTabular.TabularPreprocessorBuilder anchorBuilder = new AnchorTabular.TabularPreprocessorBuilder(false, anchorData);
        final PercentileDiscretizer percentileDiscretizer = new PercentileDiscretizer(5);
        FrameSummary frameSummary = this.dataBO.getFrame(connectionName, frameId);
        List<Map.Entry<String, Integer>> headList = new ArrayList<>(header.size());
        headList.addAll(header.entrySet());
        headList.sort(Comparator.comparingInt(Map.Entry::getValue));

        headList.forEach((entry) -> {
            String columnLabel = entry.getKey();
            ColumnSummary<?> column = findColumn(frameSummary.getColumn_summary_list(), columnLabel);
            if (columnLabel.equals(model.getTarget_column())) {
                anchorBuilder.addIgnoredColumn(columnLabel);
                //anchorBuilder.addTargetColumn(columnLabel);
            } else if (H2oUtil.isStringColumn(column.getColumn_type()) || H2oUtil.isEnumColumn(column.getColumn_type())) {
                anchorBuilder.addObjectColumn(columnLabel);
            } else {
                anchorBuilder.addNominalColumn(columnLabel, percentileDiscretizer);
            }
        });

        return anchorBuilder.build();
    }

    private ColumnSummary<?> findColumn(Collection<ColumnSummary<?>> columns, String columnName) {
        return columns.stream().filter((column) -> column.getLabel().equals(columnName)).findFirst().get();
    }

}
