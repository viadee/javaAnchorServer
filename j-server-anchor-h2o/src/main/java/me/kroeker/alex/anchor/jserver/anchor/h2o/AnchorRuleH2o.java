package me.kroeker.alex.anchor.jserver.anchor.h2o;

import de.goerke.tobias.anchorj.tabular.AnchorTabular;
import de.goerke.tobias.anchorj.tabular.CategoricalValueMapping;
import de.goerke.tobias.anchorj.tabular.ColumnDescription;
import de.goerke.tobias.anchorj.tabular.FeatureValueMapping;
import de.goerke.tobias.anchorj.tabular.MetricValueMapping;
import de.goerke.tobias.anchorj.tabular.NativeValueMapping;
import de.goerke.tobias.anchorj.tabular.TabularFeature;
import de.goerke.tobias.anchorj.tabular.TabularInstance;
import de.goerke.tobias.anchorj.tabular.TabularPerturbationFunction;
import de.viadee.anchorj.AnchorConstructionBuilder;
import de.viadee.anchorj.AnchorResult;
import de.viadee.anchorj.exploration.BatchSAR;
import de.viadee.anchorj.global.ModifiedSubmodularPick;
import de.viadee.anchorj.global.SubmodularPickGoal;
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
import me.kroeker.alex.anchor.jserver.model.AnchorConfigDescription;
import me.kroeker.alex.anchor.jserver.model.ColumnSummary;
import me.kroeker.alex.anchor.jserver.model.ContinuousColumnSummary;
import me.kroeker.alex.anchor.jserver.model.DataInstance;
import me.kroeker.alex.anchor.jserver.model.FeatureConditionEnum;
import me.kroeker.alex.anchor.jserver.model.FeatureConditionMetric;
import me.kroeker.alex.anchor.jserver.model.FrameSummary;
import me.kroeker.alex.anchor.jserver.model.Model;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import water.bindings.H2oApi;

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

@Component
public class AnchorRuleH2o implements AnchorRule {

    private ModelBO modelBO;

    private FrameBO frameBO;

    public AnchorRuleH2o(@Autowired ModelBO modelBO, @Autowired FrameBO frameBO) {
        this.modelBO = modelBO;
        this.frameBO = frameBO;
    }

    @Override
    public Collection<Anchor> runSubmodularPick(String connectionName,
                                                String modelId,
                                                String frameId,
                                                DataInstance instance) throws DataAccessException {
        final H2oApi api = H2oUtil.createH2o(connectionName);
        final LoadDataSetVH vh = loadDataSetFromH2o(frameId, api);
        final AnchorTabular.TabularPreprocessorBuilder anchorBuilder =
                buildAnchorPreprocessor(connectionName, modelId, frameId, vh);
        final AnchorTabular anchorTabular = anchorBuilder.build(vh.dataSet);
        final H2OTabularMojoClassifier classificationFunction = generateH2oClassifier(connectionName, modelId, anchorTabular);
        final TabularInstance convertedInstance = new TabularInstance(instance.getFeatureNamesMapping(), instance.getInstance());
        final TabularInstance cleanedInstance = handleInstanceToExplain(convertedInstance, vh, anchorBuilder);

        TabularPerturbationFunction tabularPerturbationFunction = new TabularPerturbationFunction(cleanedInstance,
                anchorTabular.getTabularInstances().toArray(new TabularInstance[0]));


        final AnchorConstructionBuilder<TabularInstance> anchorContructionBuilder = new AnchorConstructionBuilder<>(classificationFunction,
                tabularPerturbationFunction, cleanedInstance, classificationFunction.predict(cleanedInstance))
                .enableThreading(10, false)
                .setBestAnchorIdentification(new BatchSAR(20, 20))
                .setInitSampleCount(200)
                .setTau(0.9)
                .setAllowSuboptimalSteps(false);

        final ModifiedSubmodularPick<TabularInstance> subPick = new ModifiedSubmodularPick<>(
                anchorContructionBuilder,
                SubmodularPickGoal.FEATURE_APPEARANCE,
                10
        );
        final List<AnchorResult<TabularInstance>> anchorResults = subPick.run(
                anchorTabular.getTabularInstances().getInstances(),
                3);

        final Collection<Anchor> explanations = new ArrayList<>(anchorResults.size());
        anchorResults.forEach((anchor) -> explanations.add(transformAnchor(modelId, frameId, convertedInstance, vh,
                anchorBuilder, anchorTabular, cleanedInstance, classificationFunction, anchor)));

        return explanations;
    }

    @Override
    public Anchor computeRule(String connectionName, String modelId, String frameId, DataInstance instance)
            throws DataAccessException {
        H2oApi api = H2oUtil.createH2o(connectionName);
        LoadDataSetVH vh = loadDataSetFromH2o(frameId, api);

        AnchorTabular.TabularPreprocessorBuilder anchorBuilder = buildAnchorPreprocessor(connectionName, modelId, frameId, vh);
        AnchorTabular anchor = anchorBuilder.build(vh.dataSet);

        TabularInstance convertedInstance = new TabularInstance(instance.getFeatureNamesMapping(), instance.getInstance());
        TabularInstance cleanedInstance = handleInstanceToExplain(convertedInstance, vh, anchorBuilder);

        H2OTabularMojoClassifier classificationFunction = generateH2oClassifier(connectionName, modelId, anchor);

        TabularPerturbationFunction tabularPerturbationFunction = new TabularPerturbationFunction(cleanedInstance,
                anchor.getTabularInstances().toArray(new TabularInstance[0]));

        final AnchorResult<TabularInstance> anchorResult = new AnchorConstructionBuilder<>(classificationFunction,
                tabularPerturbationFunction, cleanedInstance, classificationFunction.predict(cleanedInstance))
                .enableThreading(10, false)
                .setBestAnchorIdentification(new BatchSAR(20, 20))
                .setInitSampleCount(200)
                .setTau(0.8)
                .setAllowSuboptimalSteps(false)
                .build().constructAnchor();

        return transformAnchor(modelId, frameId, convertedInstance, vh, anchorBuilder, anchor, cleanedInstance,
                classificationFunction, anchorResult);
    }

    @Override
    public Collection<AnchorConfigDescription> getAnchorConfigs(String connectionName) throws DataAccessException {
        return null;
    }

    private TabularInstance handleInstanceToExplain(TabularInstance instance, LoadDataSetVH vh, AnchorTabular.TabularPreprocessorBuilder anchorBuilder) {
        String[] instanceAsStringArray = Arrays.asList(instance.getInstance()).toArray(new String[0]);
        Collection<String[]> anchorInstance = new ArrayList<>(1);
        anchorInstance.add(instanceAsStringArray);
        this.handleNa(vh.header, anchorInstance, anchorBuilder.getColumnDescriptions());
        return anchorBuilder.build(anchorInstance).getTabularInstances().get(0);
    }

    private H2OTabularMojoClassifier generateH2oClassifier(String connectionName, String modelId, AnchorTabular anchor) throws DataAccessException {
        H2OTabularMojoClassifier classificationFunction;
        try (H2oDownload mojoDownload = new H2oMojoDownload()) {
            File mojoFile = mojoDownload.getFile(H2oUtil.createH2o(connectionName), modelId);

            classificationFunction = new H2OTabularMojoClassifier(
                    new FileInputStream(mojoFile),
                    anchor.getFeatures().stream().map(TabularFeature::getName).collect(Collectors.toList()));
        } catch (IOException e) {
            throw new DataAccessException("Failed to load Model MOJO with id: " + modelId + " and connection: " + connectionName);
        }
        return classificationFunction;
    }

    private Anchor transformAnchor(String modelId, String frameId, TabularInstance instance, LoadDataSetVH vh,
                                   AnchorTabular.TabularPreprocessorBuilder anchorBuilder, AnchorTabular anchor,
                                   TabularInstance convertedInstance, H2OTabularMojoClassifier classificationFunction,
                                   AnchorResult<TabularInstance> anchorResult) {
        Anchor convertedAnchor = new Anchor();
        convertedAnchor.setCoverage(anchorResult.getCoverage());
        convertedAnchor.setPrecision(anchorResult.getPrecision());
        convertedAnchor.setCreated_at(LocalDateTime.now());
        convertedAnchor.setFeatures(anchorResult.getOrderedFeatures());
        convertedAnchor.setFrame_id(frameId);
        convertedAnchor.setModel_id(modelId);

        ColumnDescription targetColumn = anchorBuilder.getColumnDescriptions().stream().filter(ColumnDescription::isTargetFeature)
                .findFirst().orElseThrow(() -> new IllegalArgumentException("no column with target definition found"));
        Object labelOfCase = instance.getInstance()[vh.header.get(targetColumn.getName())];
        convertedAnchor.setLabel_of_case(labelOfCase);

        Map<String, Object> convertedInstanceMap = new HashMap<>(instance.getFeatureCount());
        for (int i = 0; i < convertedInstance.getFeatureCount(); i++) {
            convertedInstanceMap.put(anchor.getFeatures().get(i).getName(), instance.getValue(i));
        }
        convertedAnchor.setInstance(convertedInstanceMap);

        final int affectedRows = (int) Math.round(vh.dataSet.size() * convertedAnchor.getCoverage());
        convertedAnchor.setAffected_rows(affectedRows);

        String prediction = classificationFunction.getModelWrapper().getResponseDomainValues()[anchorResult.getLabel()];
        convertedAnchor.setPrediction(prediction);

        final Map<Integer, FeatureConditionEnum> enumConditions = new HashMap<>();
        final Map<Integer, FeatureConditionMetric> metricConditions = new HashMap<>();
        for (Map.Entry<Integer, FeatureValueMapping> entry : anchor.getVisualizer().getAnchor(anchorResult).entrySet()) {
            final TabularFeature feature = entry.getValue().getFeature();
            final FeatureValueMapping featureValueMapping = entry.getValue();
            if (featureValueMapping instanceof CategoricalValueMapping) {
                enumConditions.put(entry.getKey(), new FeatureConditionEnum(feature.getName(),
                        ((CategoricalValueMapping) featureValueMapping).getCategoricalValue().toString()));
            } else if (featureValueMapping instanceof NativeValueMapping) {
                enumConditions.put(entry.getKey(), new FeatureConditionEnum(feature.getName(),
                        ((NativeValueMapping) featureValueMapping).getValue().toString()));
            } else if (featureValueMapping instanceof MetricValueMapping) {
                MetricValueMapping metric = (MetricValueMapping) featureValueMapping;
                metricConditions.put(entry.getKey(), new FeatureConditionMetric(feature.getName(),
                        metric.getMinValue(), metric.getMaxValue()));
            } else {
                throw new IllegalArgumentException("feature value mapping of type " +
                        featureValueMapping.getClass().getSimpleName() + " not handeld");
            }
        }
        convertedAnchor.setEnumAnchor(enumConditions);
        convertedAnchor.setMetricAnchor(metricConditions);
        return convertedAnchor;
    }

    private AnchorTabular.TabularPreprocessorBuilder buildAnchorPreprocessor(String connectionName,
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
            } else if (H2oUtil.isEnumColumn(column.getColumn_type())) {
                anchorBuilder.addCategoricalColumn(columnLabel);
            } else if (H2oUtil.isStringColumn(column.getColumn_type())) {
                anchorBuilder.addObjectColumn(columnLabel);
            } else {
                double min = ((ContinuousColumnSummary) column).getColumn_min();
                double max = ((ContinuousColumnSummary) column).getColumn_max();
                Function<Number[], Integer[]> discretizer = new PercentileRangeDiscretizer(5, min, max);
                anchorBuilder.addNominalColumn(columnLabel, discretizer);
            }
        });
        handleNa(vh.header, vh.dataSet, anchorBuilder.getColumnDescriptions());

        return anchorBuilder;
    }

    /**
     * Iterates through all nominal columns and replaces the empty strings with the value of @{@link NoValueHandler#getNumberNa()}
     *
     * @param header             the list of header and their column index
     * @param dataSet            the data set
     * @param columnDescriptions list of the columns
     */
    private void handleNa(Map<String, Integer> header, Collection<String[]> dataSet, List<ColumnDescription> columnDescriptions) {
        final List<Integer> nominalColumnsIndexes = columnDescriptions.stream()
                .filter((predicate) -> predicate.getColumnType() == TabularFeature.ColumnType.NOMINAL)
                .mapToInt((description) -> header.get(description.getName()))
                .boxed().collect(Collectors.toList());

        dataSet.parallelStream().forEach((dataEntry) ->
                nominalColumnsIndexes
                        .forEach((nominalColumnIndex) -> {
                            if (dataEntry[nominalColumnIndex].isEmpty()) {
                                dataEntry[nominalColumnIndex] = NoValueHandler.getNumberNa();
                            }
                        })
        );
    }

    private LoadDataSetVH loadDataSetFromH2o(String frameId, H2oApi api) throws DataAccessException {
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
        } catch (IOException ioe) {
            throw new DataAccessException("Failed to load data set from h2o with frame id: " + frameId, ioe);
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

    private ColumnSummary<?> findColumn(final Collection<ColumnSummary<?>> columns, final String columnName) {
        return columns.stream().filter((column) -> column.getLabel().equals(columnName)).findFirst().orElseThrow(
                () -> new IllegalStateException("Column with name " + columnName + " not found"));
    }

}
