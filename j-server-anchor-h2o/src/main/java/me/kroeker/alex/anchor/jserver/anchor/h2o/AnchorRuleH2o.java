package me.kroeker.alex.anchor.jserver.anchor.h2o;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import de.goerke.tobias.anchorj.base.AnchorConstructionBuilder;
import de.goerke.tobias.anchorj.base.AnchorResult;
import de.goerke.tobias.anchorj.base.exploration.BatchSAR;
import de.goerke.tobias.anchorj.tabular.AnchorTabular;
import de.goerke.tobias.anchorj.tabular.ColumnDescription;
import de.goerke.tobias.anchorj.tabular.TabularFeature;
import de.goerke.tobias.anchorj.tabular.TabularInstance;
import de.goerke.tobias.anchorj.tabular.TabularPerturbationFunction;
import me.kroeker.alex.anchor.h2o.util.H2oDataUtil;
import me.kroeker.alex.anchor.h2o.util.H2oDownload;
import me.kroeker.alex.anchor.h2o.util.H2oFrameDownload;
import me.kroeker.alex.anchor.h2o.util.H2oMojoDownload;
import me.kroeker.alex.anchor.h2o.util.H2oUtil;
import me.kroeker.alex.anchor.jserver.anchor.AnchorRule;
import me.kroeker.alex.anchor.jserver.api.exceptions.DataAccessException;
import me.kroeker.alex.anchor.jserver.business.FrameBO;
import me.kroeker.alex.anchor.jserver.business.ModelBO;
import me.kroeker.alex.anchor.jserver.model.Anchor;
import me.kroeker.alex.anchor.jserver.model.ColumnSummary;
import me.kroeker.alex.anchor.jserver.model.ContinuousColumnSummary;
import me.kroeker.alex.anchor.jserver.model.FrameSummary;
import me.kroeker.alex.anchor.jserver.model.Model;
import water.bindings.H2oApi;

@Component
public class AnchorRuleH2o implements AnchorRule {

    private ModelBO modelBO;

    private FrameBO frameBO;

    public AnchorRuleH2o(@Autowired ModelBO modelBO, @Autowired FrameBO frameBO) {
        this.modelBO = modelBO;
        this.frameBO = frameBO;
    }

    @Override
    public Anchor computeRule(String connectionName, String modelId, String frameId, TabularInstance instance)
            throws DataAccessException {
        H2oApi api = H2oUtil.createH2o(connectionName);

        LoadDataSetVH vh;
        try {
            vh = loadDataSetFromH2o(frameId, api);
        } catch (IOException ioe) {
            throw new DataAccessException("Failed to load data set from h2o with connection name: "
                    + connectionName + " and frame id: " + frameId, ioe);
        }

        AnchorTabular.TabularPreprocessorBuilder anchorBuilder = buildAnchor(connectionName, modelId, frameId, vh);
        AnchorTabular anchor = anchorBuilder.build(vh.dataSet);

        String[] instanceAsStringArray = Arrays.asList(instance.getInstance()).toArray(new String[0]);
        Collection<String[]> anchorInstance = new ArrayList<>(1);
        anchorInstance.add(instanceAsStringArray);
        TabularInstance convertedInstance = anchorBuilder.build(anchorInstance).getTabularInstances().get(0);

        try (H2oDownload mojoDownload = new H2oMojoDownload()) {
            File mojoFile = mojoDownload.getFile(H2oUtil.createH2o(connectionName), modelId);

            final H2OTabularMojoClassifier classificationFunction = new H2OTabularMojoClassifier(
                    new FileInputStream(mojoFile),
                    anchor.getFeatures().stream().map(TabularFeature::getName).collect(Collectors.toList()));

            TabularPerturbationFunction tabularPerturbationFunction = new TabularPerturbationFunction(instance,
                    anchor.getTabularInstances().toArray(new TabularInstance[0]));

            final AnchorResult<TabularInstance> anchorResult = new AnchorConstructionBuilder<>(classificationFunction,
                    tabularPerturbationFunction, convertedInstance,
                    classificationFunction.predict(convertedInstance))
                    .enableThreading(10, false)
                    .setBestAnchorIdentification(new BatchSAR(20, 20))
                    .setInitSampleCount(500)
                    .setTau(0.8)
                    .build().constructAnchor();

            Anchor computedAnchor = new Anchor();
            computedAnchor.setCoverage(anchorResult.getCoverage());
            computedAnchor.setPrecision(anchorResult.getPrecision());
            computedAnchor.setCreated_at(LocalDateTime.now());
            computedAnchor.setFeatures(anchorResult.getOrderedFeatures());
            computedAnchor.setFrame_id(frameId);
            computedAnchor.setModel_id(modelId);

            ColumnDescription targetColumn = anchorBuilder.getColumnDescriptions().stream().filter(ColumnDescription::isTargetFeature)
                    .findFirst().orElseThrow(() -> new IllegalArgumentException("no column with target definition found"));
            Object labelOfCase = instance.getInstance()[vh.header.get(targetColumn.getName())];
            computedAnchor.setLabel_of_case(labelOfCase);

            Map<String, Object> convertedInstanceMap = new HashMap<>(instance.getFeatureCount());
            for (int i = 0; i < convertedInstance.getFeatureCount(); i++) {
                convertedInstanceMap.put(anchor.getFeatures().get(i).getName(), instance.getFeature(i));
            }
            computedAnchor.setInstance(convertedInstanceMap);

            final int affectedRows = (int) Math.round(vh.dataSet.size() * computedAnchor.getCoverage());
            computedAnchor.setAffected_rows(affectedRows);

            String prediction = classificationFunction.getModelWrapper().getResponseDomainValues()[anchorResult.getLabel()];
            computedAnchor.setPrediction(prediction);
            computedAnchor.setNames(Arrays.asList(anchor.getVisualizer().getAnchorAsPredicateList(anchorResult)));

            return computedAnchor;
        } catch (IOException e) {
            throw new DataAccessException("Failed to load Model MOJO with id: " + modelId + " and connection: " + connectionName);
        }
    }

    private AnchorTabular.TabularPreprocessorBuilder buildAnchor(String connectionName,
                                                                 String modelId,
                                                                 String frameId,
                                                                 LoadDataSetVH vh) throws DataAccessException {
        Model model = this.modelBO.getModel(connectionName, modelId);

        AnchorTabular.TabularPreprocessorBuilder anchorBuilder =
                new AnchorTabular.TabularPreprocessorBuilder();

        FrameSummary frameSummary = this.frameBO.getFrameSummary(connectionName, frameId);
        List<Map.Entry<String, Integer>> headList = new ArrayList<>(vh.header.size());
        headList.addAll(vh.header.entrySet());
        headList.sort(Comparator.comparingInt(Map.Entry::getValue));

        headList.forEach((entry) -> {
            String columnLabel = entry.getKey();
            ColumnSummary<?> column = findColumn(frameSummary.getColumn_summary_list(), columnLabel);
            if (columnLabel.equals(model.getTarget_column())) {
                anchorBuilder.addTargetColumn(columnLabel);
            } else if (H2oUtil.isStringColumn(column.getColumn_type())
                    || H2oUtil.isEnumColumn(column.getColumn_type())) {
                anchorBuilder.addObjectColumn(columnLabel);
            } else {
                double min = ((ContinuousColumnSummary) column).getColumn_min();
                double max = ((ContinuousColumnSummary) column).getColumn_max();
                Function<Number[], Integer[]> discretizer = new PercentileRangeDiscretizer(5, min, max);
                anchorBuilder.addNominalColumn(columnLabel, discretizer);
            }
        });

        return anchorBuilder;
    }

    private LoadDataSetVH loadDataSetFromH2o(String frameId, H2oApi api) throws IOException {
        Collection<String[]> dataSet = new ArrayList<>();
        Map<String, Integer> header;
        try (H2oFrameDownload h2oDownload = new H2oFrameDownload()) {
            File dataSetFile = h2oDownload.getFile(api, frameId);
            Collection<String> newLine = new ArrayList<>();
            header = H2oDataUtil.iterateThroughCsvData(dataSetFile, (record) -> {
                        newLine.clear();
                        record.iterator().forEachRemaining(newLine::add);
                        dataSet.add(newLine.toArray(new String[0]));
                    }
            );
        }
        return new LoadDataSetVH(header, dataSet);
    }

    private static class LoadDataSetVH {
        final Map<String, Integer> header;
        final Collection<String[]> dataSet;

        LoadDataSetVH(Map<String, Integer> header, Collection<String[]> dataSet) {
            this.header = header;
            this.dataSet = dataSet;
        }
    }

    private ColumnSummary<?> findColumn(Collection<ColumnSummary<?>> columns, String columnName) {
        return columns.stream().filter((column) -> column.getLabel().equals(columnName)).findFirst().get();
    }

}
