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
import de.viadee.anchorj.AnchorConstructionBuilder;
import de.viadee.anchorj.AnchorResult;
import de.viadee.anchorj.exploration.BatchSAR;
import de.viadee.anchorj.global.ModifiedSubmodularPick;
import de.viadee.anchorj.tabular.AnchorTabular;
import de.viadee.anchorj.tabular.CategoricalValueMapping;
import de.viadee.anchorj.tabular.ColumnDescription;
import de.viadee.anchorj.tabular.FeatureValueMapping;
import de.viadee.anchorj.tabular.MetricValueMapping;
import de.viadee.anchorj.tabular.NativeValueMapping;
import de.viadee.anchorj.tabular.TabularFeature;
import de.viadee.anchorj.tabular.TabularInstance;
import de.viadee.anchorj.tabular.TabularPerturbationFunction;
import hex.genmodel.easy.prediction.AbstractPrediction;
import hex.genmodel.easy.prediction.BinomialModelPrediction;
import hex.genmodel.easy.prediction.MultinomialModelPrediction;
import me.kroeker.alex.anchor.jserver.anchor.AnchorRule;
import me.kroeker.alex.anchor.jserver.api.exceptions.DataAccessException;
import me.kroeker.alex.anchor.jserver.business.FrameBO;
import me.kroeker.alex.anchor.jserver.business.ModelBO;
import me.kroeker.alex.anchor.jserver.h2o.util.H2oConnector;
import me.kroeker.alex.anchor.jserver.h2o.util.H2oDataUtil;
import me.kroeker.alex.anchor.jserver.h2o.util.H2oDownload;
import me.kroeker.alex.anchor.jserver.h2o.util.H2oFrameDownload;
import me.kroeker.alex.anchor.jserver.h2o.util.H2oMojoDownload;
import me.kroeker.alex.anchor.jserver.h2o.util.H2oUtil;
import me.kroeker.alex.anchor.jserver.model.Anchor;
import me.kroeker.alex.anchor.jserver.model.AnchorConfigDescription;
import me.kroeker.alex.anchor.jserver.model.ColumnSummary;
import me.kroeker.alex.anchor.jserver.model.ContinuousColumnSummary;
import me.kroeker.alex.anchor.jserver.model.FeatureConditionEnum;
import me.kroeker.alex.anchor.jserver.model.FeatureConditionMetric;
import me.kroeker.alex.anchor.jserver.model.FrameInstance;
import me.kroeker.alex.anchor.jserver.model.FrameSummary;
import me.kroeker.alex.anchor.jserver.model.Model;
import water.bindings.H2oApi;

@Component
public class AnchorRuleH2o implements AnchorRule, H2oConnector {

    private static final String ANCHOR_TAU = "Tau";
    private static final String ANCHOR_DELTA = "Delta";
    private static final String ANCHOR_EPSILON = "Epsilon";
    private static final String ANCHOR_TAU_DISCREPANCY = "Tau-Discrepancy";
    private static final String ANCHOR_BUCKET_NO = "Bucket-No.";

    private static final Map<String, AnchorConfigDescription> DEFAULT_ANCHOR_PARAMS;

    static {
        DEFAULT_ANCHOR_PARAMS = new HashMap<>();
        DEFAULT_ANCHOR_PARAMS.put(ANCHOR_TAU, new AnchorConfigDescription(ANCHOR_TAU,
                AnchorConfigDescription.ConfigInputType.DOUBLE, 0.9)
        );
        DEFAULT_ANCHOR_PARAMS.put(ANCHOR_DELTA, new AnchorConfigDescription(ANCHOR_DELTA,
                AnchorConfigDescription.ConfigInputType.DOUBLE, 0.1)
        );
        DEFAULT_ANCHOR_PARAMS.put(ANCHOR_EPSILON, new AnchorConfigDescription(ANCHOR_EPSILON,
                AnchorConfigDescription.ConfigInputType.DOUBLE, 0.1)
        );
        DEFAULT_ANCHOR_PARAMS.put(ANCHOR_TAU_DISCREPANCY, new AnchorConfigDescription(ANCHOR_TAU_DISCREPANCY,
                AnchorConfigDescription.ConfigInputType.DOUBLE, 0.05)
        );
        DEFAULT_ANCHOR_PARAMS.put(ANCHOR_BUCKET_NO, new AnchorConfigDescription(ANCHOR_BUCKET_NO,
                AnchorConfigDescription.ConfigInputType.INTEGER, 5)
        );
    }

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
                                                FrameInstance instance,
                                                Map<String, Object> anchorConfig) throws DataAccessException {
        final H2oApi api = this.createH2o(connectionName);
        final LoadDataSetVH vh = loadDataSetFromH2o(frameId, api);

        final int bucketNo = (Integer) getAnchorOptionFromParamsOrDefault(anchorConfig, ANCHOR_BUCKET_NO);

        final AnchorTabular.TabularPreprocessorBuilder anchorBuilder =
                buildAnchorPreprocessor(connectionName, modelId, frameId, vh, bucketNo);
        final AnchorTabular anchorTabular = anchorBuilder.build(vh.dataSet);
        final H2oTabularMojoClassifier classificationFunction = generateH2oClassifier(connectionName, modelId, anchorTabular);
        final TabularInstance convertedInstance = new TabularInstance(instance.getFeatureNamesMapping(), instance.getInstance());
        final TabularInstance cleanedInstance = handleInstanceToExplain(convertedInstance, vh, anchorBuilder);

        TabularPerturbationFunction tabularPerturbationFunction = new TabularPerturbationFunction(cleanedInstance,
                anchorTabular.getTabularInstances().toArray(new TabularInstance[0]));

        final double anchorTau = (Double) getAnchorOptionFromParamsOrDefault(anchorConfig, ANCHOR_TAU);
        final double anchorDelta = (Double) getAnchorOptionFromParamsOrDefault(anchorConfig, ANCHOR_DELTA);
        final double anchorEpsilon = (Double) getAnchorOptionFromParamsOrDefault(anchorConfig, ANCHOR_EPSILON);
        final double anchorTauDiscrepancy = (Double) getAnchorOptionFromParamsOrDefault(anchorConfig, ANCHOR_TAU_DISCREPANCY);

        final AnchorConstructionBuilder<TabularInstance> anchorConstructionBuilder = new AnchorConstructionBuilder<>(classificationFunction,
                tabularPerturbationFunction, cleanedInstance, classificationFunction.predict(cleanedInstance))
                .enableThreading(10, false)
                .setBestAnchorIdentification(new BatchSAR(100 * anchorTabular.getFeatures().size(), 10))
                .setInitSampleCount(200)
                .setTau(anchorTau)
                .setDelta(anchorDelta)
                .setEpsilon(anchorEpsilon)
                .setTauDiscrepancy(anchorTauDiscrepancy)
                .setAllowSuboptimalSteps(true);

        final ModifiedSubmodularPick<TabularInstance> subPick = new ModifiedSubmodularPick<>(
                anchorConstructionBuilder,
                10
        );
        final List<AnchorResult<TabularInstance>> anchorResults = subPick.run(
                anchorTabular.getTabularInstances().getInstances(),
                3);

        final Collection<Anchor> explanations = new ArrayList<>(anchorResults.size());
        anchorResults.forEach((anchor) -> explanations.add(transformAnchor(modelId, frameId, cleanedInstance, vh,
                anchorBuilder, anchorTabular, convertedInstance, classificationFunction, anchor)));

        return explanations;
    }

    @Override
    public Anchor computeRule(String connectionName, String modelId, String frameId, FrameInstance instance,
                              Map<String, Object> anchorConfig)
            throws DataAccessException {
        H2oApi api = this.createH2o(connectionName);
        LoadDataSetVH vh = loadDataSetFromH2o(frameId, api);

        final int bucketNo = (Integer) getAnchorOptionFromParamsOrDefault(anchorConfig, ANCHOR_BUCKET_NO);

        AnchorTabular.TabularPreprocessorBuilder anchorBuilder = buildAnchorPreprocessor(connectionName, modelId,
                frameId, vh, bucketNo);
        AnchorTabular anchorTabular = anchorBuilder.build(vh.dataSet);

        TabularInstance convertedInstance = new TabularInstance(instance.getFeatureNamesMapping(), instance.getInstance());
        TabularInstance cleanedInstance = handleInstanceToExplain(convertedInstance, vh, anchorBuilder);

        H2oTabularMojoClassifier classificationFunction = generateH2oClassifier(connectionName, modelId, anchorTabular);

        TabularPerturbationFunction tabularPerturbationFunction = new TabularPerturbationFunction(cleanedInstance,
                anchorTabular.getTabularInstances().toArray(new TabularInstance[0]));

        final double anchorTau = (Double) getAnchorOptionFromParamsOrDefault(anchorConfig, ANCHOR_TAU);
        final double anchorDelta = (Double) getAnchorOptionFromParamsOrDefault(anchorConfig, ANCHOR_DELTA);
        final double anchorEpsilon = (Double) getAnchorOptionFromParamsOrDefault(anchorConfig, ANCHOR_EPSILON);
        final double anchorTauDiscrepancy = (Double) getAnchorOptionFromParamsOrDefault(anchorConfig, ANCHOR_TAU_DISCREPANCY);

        final AnchorResult<TabularInstance> anchorResult = new AnchorConstructionBuilder<>(classificationFunction,
                tabularPerturbationFunction, cleanedInstance, classificationFunction.predict(cleanedInstance))
                .enableThreading(10, false)
                .setBestAnchorIdentification(new BatchSAR(100 * anchorTabular.getFeatures().size(), 10))
                .setInitSampleCount(200)
                .setTau(anchorTau)
                .setDelta(anchorDelta)
                .setEpsilon(anchorEpsilon)
                .setTauDiscrepancy(anchorTauDiscrepancy)
                .setAllowSuboptimalSteps(false)
                .build().constructAnchor();

        return transformAnchor(modelId, frameId, cleanedInstance, vh, anchorBuilder, anchorTabular, convertedInstance,
                classificationFunction, anchorResult);
    }

    private static Object getAnchorOptionFromParamsOrDefault(Map<String, Object> anchorConfig, String paramName) {
        return anchorConfig.getOrDefault(paramName, DEFAULT_ANCHOR_PARAMS.get(paramName).getValue());
    }

    @Override
    public Collection<AnchorConfigDescription> getAnchorConfigs() {
        return DEFAULT_ANCHOR_PARAMS.values();
    }

    private TabularInstance handleInstanceToExplain(TabularInstance instance, LoadDataSetVH vh, AnchorTabular.TabularPreprocessorBuilder anchorBuilder) {
        @SuppressWarnings("SuspiciousToArrayCall")
        String[] instanceAsStringArray = Arrays.asList(instance.getInstance()).toArray(new String[0]);
        Collection<String[]> anchorInstance = new ArrayList<>(1);
        anchorInstance.add(instanceAsStringArray);
        this.handleNa(vh.header, anchorInstance, anchorBuilder.getColumnDescriptions());
        return anchorBuilder.build(anchorInstance).getTabularInstances().get(0);
    }

    private H2oTabularMojoClassifier generateH2oClassifier(String connectionName, String modelId, AnchorTabular anchor) throws DataAccessException {
        H2oTabularMojoClassifier classificationFunction;
        try (H2oDownload mojoDownload = new H2oMojoDownload()) {
            File mojoFile = mojoDownload.getFile(this.createH2o(connectionName), modelId);

            classificationFunction = new H2oTabularMojoClassifier(
                    new FileInputStream(mojoFile),
                    this.generateH2oPredictor(),
                    anchor.getFeatures().stream().map(TabularFeature::getName).collect(Collectors.toList()),
                    anchor.getFeatures().stream().filter((desc) -> desc.getColumnType() == TabularFeature.ColumnType.CATEGORICAL).map(TabularFeature::getName).collect(Collectors.toList()));
        } catch (IOException e) {
            throw new DataAccessException("Failed to load Model MOJO with id: " + modelId + " and connection: " + connectionName);
        }
        return classificationFunction;
    }

    /**
     * Handles {@link BinomialModelPrediction} and {@link MultinomialModelPrediction}.
     *
     * @return a function to extract the labelIndex value of the predictions
     */
    private Function<AbstractPrediction, Integer> generateH2oPredictor() {
        return (prediction) -> {
            if (prediction instanceof BinomialModelPrediction) {
                return ((BinomialModelPrediction) prediction).labelIndex;
            } else if (prediction instanceof MultinomialModelPrediction) {
                return ((MultinomialModelPrediction) prediction).labelIndex;
            } else {
                throw new UnsupportedOperationException("Prediction of type: " + prediction.getClass().getSimpleName()
                        + "; not supported");
            }
        };
    }

    private Anchor transformAnchor(String modelId, String frameId, TabularInstance cleanedInstnace, LoadDataSetVH vh,
                                   AnchorTabular.TabularPreprocessorBuilder anchorBuilder, AnchorTabular anchor,
                                   TabularInstance convertedInstance, H2oTabularMojoClassifier classificationFunction,
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
        Object labelOfCase = convertedInstance.getInstance()[vh.header.get(targetColumn.getName())];
        convertedAnchor.setLabel_of_case(labelOfCase);

        Map<String, Object> cleanedInstanceMap = new HashMap<>(cleanedInstnace.getFeatureCount());
        for (int i = 0; i < cleanedInstnace.getFeatureCount(); i++) {
            cleanedInstanceMap.put(anchor.getFeatures().get(i).getName(), cleanedInstnace.getOriginalValue(i));
        }
        convertedAnchor.setInstance(cleanedInstanceMap);

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
                String value = featureValueMapping.getValue().toString();
                enumConditions.put(entry.getKey(), new FeatureConditionEnum(feature.getName(), value));
            } else if (featureValueMapping instanceof NativeValueMapping) {
                enumConditions.put(entry.getKey(), new FeatureConditionEnum(feature.getName(),
                        featureValueMapping.getValue().toString()));
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
                                                                             LoadDataSetVH vh,
                                                                             final int classCount) throws DataAccessException {
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
            } else if (model.getIgnoredColumns().contains(columnLabel)) {
                anchorBuilder.addIgnoredColumn(columnLabel);
            } else if (H2oUtil.isEnumColumn(column.getColumn_type())) {
                anchorBuilder.addCategoricalColumn(columnLabel);
            } else if (H2oUtil.isStringColumn(column.getColumn_type())) {
                anchorBuilder.addObjectColumn(columnLabel);
            } else {
                double min = ((ContinuousColumnSummary) column).getColumn_min();
                double max = ((ContinuousColumnSummary) column).getColumn_max();
                Function<Number[], Integer[]> discretizer = new PercentileRangeDiscretizer(classCount, min, max);
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
                                dataEntry[nominalColumnIndex] = String.valueOf(NoValueHandler.getNumberNa());
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
