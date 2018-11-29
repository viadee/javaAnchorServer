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
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import de.viadee.anchorj.AnchorCandidate;
import de.viadee.anchorj.AnchorConstructionBuilder;
import de.viadee.anchorj.AnchorResult;
import de.viadee.anchorj.exploration.BatchSAR;
import de.viadee.anchorj.global.AbstractGlobalExplainer;
import de.viadee.anchorj.global.CoveragePick;
import de.viadee.anchorj.global.ReconfigurablePerturbationFunction;
import de.viadee.anchorj.tabular.AnchorTabular;
import de.viadee.anchorj.tabular.CategoricalValueMapping;
import de.viadee.anchorj.tabular.ColumnDescription;
import de.viadee.anchorj.tabular.FeatureValueMapping;
import de.viadee.anchorj.tabular.MetricValueMapping;
import de.viadee.anchorj.tabular.NativeValueMapping;
import de.viadee.anchorj.tabular.TabularFeature;
import de.viadee.anchorj.tabular.TabularInstance;
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
import me.kroeker.alex.anchor.jserver.model.AnchorRuleEnum;
import me.kroeker.alex.anchor.jserver.model.AnchorRuleMetric;
import me.kroeker.alex.anchor.jserver.model.ColumnSummary;
import me.kroeker.alex.anchor.jserver.model.ContinuousColumnSummary;
import me.kroeker.alex.anchor.jserver.model.FrameInstance;
import me.kroeker.alex.anchor.jserver.model.FrameSummary;
import me.kroeker.alex.anchor.jserver.model.Model;
import me.kroeker.alex.anchor.jserver.model.SubmodularPickResult;
import water.bindings.H2oApi;

@Component
public class AnchorRuleH2o implements AnchorRule, H2oConnector {

    private static final Logger LOG = LoggerFactory.getLogger(AnchorRuleH2o.class);

    private static final String ANCHOR_TAU = "Tau";
    private static final String ANCHOR_DELTA = "Delta";
    private static final String ANCHOR_EPSILON = "Epsilon";
    private static final String ANCHOR_TAU_DISCREPANCY = "Tau-Discrepancy";
    private static final String ANCHOR_BUCKET_NO = "Bucket-No.";
    //    private static final String SP_SAMPLE_SIZE = "Sample-Size";
    private static final String SP_NO_ANCHOR = "No-Anchor";

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
//        DEFAULT_ANCHOR_PARAMS.put(SP_SAMPLE_SIZE, new AnchorConfigDescription(SP_SAMPLE_SIZE,
//                AnchorConfigDescription.ConfigInputType.INTEGER, Integer.MAX_VALUE)
//        );
        DEFAULT_ANCHOR_PARAMS.put(SP_NO_ANCHOR, new AnchorConfigDescription(SP_NO_ANCHOR,
                AnchorConfigDescription.ConfigInputType.INTEGER, 3)
        );
    }

    private ModelBO modelBO;

    private FrameBO frameBO;

    public AnchorRuleH2o(@Autowired ModelBO modelBO, @Autowired FrameBO frameBO) {
        this.modelBO = modelBO;
        this.frameBO = frameBO;
    }

    @Override
    public Collection<AnchorConfigDescription> getAnchorConfigs() {
        return DEFAULT_ANCHOR_PARAMS.values();
    }

    @Override
    public SubmodularPickResult runSubmodularPick(String connectionName,
                                                  String modelId,
                                                  String frameId,
                                                  FrameInstance instance,
                                                  Map<String, Object> anchorConfig) throws DataAccessException {
        final H2oApi api = this.createH2o(connectionName);
        LoadDataSetVH vh = loadDataSetFromH2o(frameId, api);

        final int bucketNo = (Integer) getAnchorOptionFromParamsOrDefault(anchorConfig, ANCHOR_BUCKET_NO);
        final int noAnchor = (Integer) getAnchorOptionFromParamsOrDefault(anchorConfig, SP_NO_ANCHOR);

        final AnchorTabular.TabularPreprocessorBuilder anchorBuilder =
                buildAnchorPreprocessor(connectionName, modelId, frameId, vh, bucketNo);
//        SPRandomSelection randomSelection = new SPRandomSelection(new Random(), vh.dataSet);

//        final int sampleSize = (Integer) getAnchorOptionFromParamsOrDefault(anchorConfig, SP_SAMPLE_SIZE);
        final AnchorTabular anchorTabular = anchorBuilder.build(vh.dataSet);

        final int dataSetSize = vh.dataSet.size();
        // garbage!
        vh = null;

        final H2oTabularMojoClassifier classificationFunction = generateH2oClassifier(connectionName, modelId, anchorTabular);
        final TabularInstance convertedInstance = new TabularInstance(instance.getFeatureNamesMapping(), instance.getInstance());
        final TabularInstance cleanedInstance = handleInstanceToExplain(convertedInstance, anchorBuilder);

        final AnchorConstructionBuilder<TabularInstance> anchorConstructionBuilder = createAnchorBuilderWithConfig(anchorTabular, classificationFunction, cleanedInstance, anchorConfig);

        final AbstractGlobalExplainer<TabularInstance> subPick = new CoveragePick<>(anchorConstructionBuilder, 10);
//        final AbstractGlobalExplainer<TabularInstance> subPick = new ModifiedSubmodularPick<>(
//                anchorConstructionBuilder,
//                10
//        );
        final List<AnchorResult<TabularInstance>> anchorResults = subPick.run(
                anchorTabular.getTabularInstances().getInstances(),
                noAnchor);


        final Collection<Anchor> explanations = new ArrayList<>(anchorResults.size());
        anchorResults.forEach((anchorResult) -> explanations.add(transformAnchor(modelId, frameId,
                dataSetSize, anchorBuilder, anchorTabular, classificationFunction, anchorResult)));

        Set<TabularInstance> filteredInstances = new HashSet<>();
        this.runParallel(anchorTabular.getTabularInstances(), (item) -> {
            anchorResults.forEach((result) -> {
                result.getOrderedFeatures().forEach((featureIndex) -> {
                    if (item.getValue(featureIndex).equals(result.getInstance().getValue(featureIndex))) {
                        filteredInstances.add(item);
                    }
                });
            });
        });

        final double aggregatedCoverage = filteredInstances.size() / (double) dataSetSize;
        return new SubmodularPickResult(explanations, aggregatedCoverage);
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
        final int dataSetSize = vh.dataSet.size();
        // garbage!
        vh = null;

        TabularInstance convertedInstance = new TabularInstance(instance.getFeatureNamesMapping(), instance.getInstance(), instance.getInstance());
        TabularInstance cleanedInstance = handleInstanceToExplain(convertedInstance, anchorBuilder);

        H2oTabularMojoClassifier classificationFunction = generateH2oClassifier(connectionName, modelId, anchorTabular);

        final AnchorResult<TabularInstance> anchorResult =
                this.createAnchorBuilderWithConfig(anchorTabular, classificationFunction, cleanedInstance, anchorConfig)
                        .build().constructAnchor();

        return transformAnchor(modelId, frameId, dataSetSize, anchorBuilder, anchorTabular,
                classificationFunction, anchorResult);
    }

    private AnchorConstructionBuilder<TabularInstance> createAnchorBuilderWithConfig(AnchorTabular anchorTabular, H2oTabularMojoClassifier classificationFunction, TabularInstance cleanedInstance, Map<String, Object> anchorConfig) {
        ReconfigurablePerturbationFunction<TabularInstance> tabularPerturbationFunction = new TabularPertubationWithOriginalDataFunction(cleanedInstance,
                anchorTabular.getTabularInstances().toArray(new TabularInstance[0]));

        final double anchorTau = (Double) getAnchorOptionFromParamsOrDefault(anchorConfig, ANCHOR_TAU);
        final double anchorDelta = (Double) getAnchorOptionFromParamsOrDefault(anchorConfig, ANCHOR_DELTA);
        final double anchorEpsilon = (Double) getAnchorOptionFromParamsOrDefault(anchorConfig, ANCHOR_EPSILON);
        final double anchorTauDiscrepancy = (Double) getAnchorOptionFromParamsOrDefault(anchorConfig, ANCHOR_TAU_DISCREPANCY);

        return new AnchorConstructionBuilder<>(classificationFunction,
                tabularPerturbationFunction, cleanedInstance, classificationFunction.predict(cleanedInstance))
                .setBestAnchorIdentification(new BatchSAR(100 * anchorTabular.getFeatures().size(), 10))
                .setInitSampleCount(100)
                .setTau(anchorTau)
                .setDelta(anchorDelta)
                .setEpsilon(anchorEpsilon)
                .setTauDiscrepancy(anchorTauDiscrepancy)
                .setAllowSuboptimalSteps(true);
    }

    private static Object getAnchorOptionFromParamsOrDefault(Map<String, Object> anchorConfig, String paramName) {
        return anchorConfig.getOrDefault(paramName, DEFAULT_ANCHOR_PARAMS.get(paramName).getValue());
    }

    private TabularInstance handleInstanceToExplain(TabularInstance instance,
                                                    AnchorTabular.TabularPreprocessorBuilder anchorBuilder) {
        @SuppressWarnings("SuspiciousToArrayCall")
        String[] instanceAsStringArray = Arrays.asList(instance.getInstance()).toArray(new String[0]);
        Collection<String[]> anchorInstance = new ArrayList<>(1);
        anchorInstance.add(instanceAsStringArray);
        this.handleNa(instance.getFeatureNamesMapping(), anchorInstance, anchorBuilder.getColumnDescriptions());
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
                    anchor.getFeatures().stream().filter((desc) -> desc.getColumnType() == TabularFeature.ColumnType.CATEGORICAL).map(TabularFeature::getName).collect(Collectors.toList()),
                    anchor.getTabularInstances().size());
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

    private Anchor transformAnchor(String modelId, String frameId, int dataSetSize,
                                   AnchorTabular.TabularPreprocessorBuilder anchorBuilder, AnchorTabular anchor,
                                   H2oTabularMojoClassifier classificationFunction,
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

        TabularInstance cleanedInstance = anchorResult.getInstance();
        Object labelOfCase = cleanedInstance.getOriginalInstance()
                [cleanedInstance.getFeatureNamesMapping().get(targetColumn.getName())];
        convertedAnchor.setLabel_of_case(labelOfCase);

        Map<String, Object> cleanedInstanceMap = new HashMap<>(cleanedInstance.getFeatureCount());
        for (int i = 0; i < cleanedInstance.getFeatureCount(); i++) {
            cleanedInstanceMap.put(anchor.getFeatures().get(i).getName(), cleanedInstance.getOriginalValue(i));
        }
        convertedAnchor.setInstance(cleanedInstanceMap);

        final int affectedRows = (int) Math.round(dataSetSize * convertedAnchor.getCoverage());
        convertedAnchor.setAffected_rows(affectedRows);

        String prediction = classificationFunction.getModelWrapper().getResponseDomainValues()[anchorResult.getLabel()];
        convertedAnchor.setPrediction(prediction);

        final Map<Integer, AnchorRuleEnum> enumAnchors = new HashMap<>();
        final Map<Integer, AnchorRuleMetric> metricAnchors = new HashMap<>();
        for (Map.Entry<Integer, FeatureValueMapping> entry : anchor.getVisualizer().getAnchor(anchorResult).entrySet()) {
            final TabularFeature feature = entry.getValue().getFeature();

            final int featureIndex = anchorResult.getInstance().getFeatureArrayIndex(feature.getName());
            final AnchorCandidate candidate = this.findCandidate(anchorResult, featureIndex);
            double addedCoverage = 0;
            double addedPrecision = 0;
            if (candidate != null) {
                addedCoverage = candidate.getAddedCoverage();
                addedPrecision = candidate.getAddedPrecision();
            } else {
                LOG.error("No AnchorCandidate for feature with index " + featureIndex + " and name " + feature.getName() + " found");
            }

            final FeatureValueMapping featureValueMapping = entry.getValue();
            if (featureValueMapping instanceof CategoricalValueMapping) {
                String value = featureValueMapping.getValue().toString();
                enumAnchors.put(entry.getKey(), new AnchorRuleEnum(feature.getName(), value, addedPrecision, addedCoverage));
            } else if (featureValueMapping instanceof NativeValueMapping) {
                enumAnchors.put(entry.getKey(), new AnchorRuleEnum(feature.getName(),
                        featureValueMapping.getValue().toString(), addedPrecision, addedCoverage));
            } else if (featureValueMapping instanceof MetricValueMapping) {
                MetricValueMapping metric = (MetricValueMapping) featureValueMapping;
                metricAnchors.put(entry.getKey(), new AnchorRuleMetric(feature.getName(),
                        metric.getMinValue(), metric.getMaxValue(), addedPrecision, addedCoverage));
            } else {
                throw new IllegalArgumentException("feature value mapping of type " +
                        featureValueMapping.getClass().getSimpleName() + " not handled");
            }
        }
        convertedAnchor.setEnumAnchor(enumAnchors);
        convertedAnchor.setMetricAnchor(metricAnchors);
        return convertedAnchor;
    }

    private AnchorCandidate findCandidate(AnchorCandidate candidate, Integer feature) {
        if (candidate.getAddedFeature().equals(feature)) {
            return candidate;
        } else if (candidate.getParentCandidate() != null) {
            return findCandidate(candidate.getParentCandidate(), feature);
        } else {
            return null;
        }
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

        this.runParallel(dataSet, (dataEntry) ->
                nominalColumnsIndexes.forEach((nominalColumnIndex) -> {
                    if (dataEntry[nominalColumnIndex].isEmpty()) {
                        dataEntry[nominalColumnIndex] = String.valueOf(NoValueHandler.getNumberNa());
                    }
                })
        );
    }

    private <T> void runParallel(Collection<T> list, Consumer<T> consumer) {
        ForkJoinPool fjp = new ForkJoinPool(10, ForkJoinPool.defaultForkJoinWorkerThreadFactory, new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread t, Throwable e) {
                LOG.error("uncaught exception of thread: " + t.getName() + " and error: " + e.getMessage(), e);
            }
        }, false);
        try {
            fjp.submit(() -> {
                list.parallelStream().forEach(consumer);
            }).get();
        } catch (InterruptedException | ExecutionException e) {
            LOG.error("interrupted filter", e);
        } finally {
            fjp.shutdown();
        }
    }

    private LoadDataSetVH loadDataSetFromH2o(String frameId, H2oApi api) throws DataAccessException {
        List<String[]> dataSet = new ArrayList<>();
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
        final List<String[]> dataSet;

        LoadDataSetVH(Map<String, Integer> header, List<String[]> dataSet) {
            this.header = header;
            this.dataSet = dataSet;
        }
    }

    private ColumnSummary<?> findColumn(final Collection<ColumnSummary<?>> columns, final String columnName) {
        return columns.stream().filter((column) -> column.getLabel().equals(columnName)).findFirst().orElseThrow(
                () -> new IllegalStateException("Column with name " + columnName + " not found"));
    }

}
