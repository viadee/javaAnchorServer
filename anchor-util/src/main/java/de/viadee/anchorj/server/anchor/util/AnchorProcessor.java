package de.viadee.anchorj.server.anchor.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import de.viadee.anchorj.AnchorConstructionBuilder;
import de.viadee.anchorj.AnchorResult;
import de.viadee.anchorj.ClassificationFunction;
import de.viadee.anchorj.exploration.BatchSAR;
import de.viadee.anchorj.global.AbstractGlobalExplainer;
import de.viadee.anchorj.global.ReconfigurablePerturbationFunction;
import de.viadee.anchorj.server.api.exceptions.DataAccessException;
import de.viadee.anchorj.server.business.FrameBO;
import de.viadee.anchorj.server.business.ModelBO;
import de.viadee.anchorj.server.h2o.util.H2oDataUtil;
import de.viadee.anchorj.server.model.Anchor;
import de.viadee.anchorj.server.model.FrameInstance;
import de.viadee.anchorj.server.model.FrameSummary;
import de.viadee.anchorj.server.model.Model;
import de.viadee.anchorj.server.model.SubmodularPickResult;
import de.viadee.anchorj.tabular.AnchorTabular;
import de.viadee.anchorj.tabular.TabularFeature;
import de.viadee.anchorj.tabular.TabularInstance;
import water.bindings.H2oApi;

/**
 *
 */
public class AnchorProcessor {

    private final H2oApi api;
    private final ModelBO modelBO;
    private final FrameBO frameBO;
    private final Map<String, Object> anchorConfig;
    private final String modelId;
    private final String frameId;
    private final String connectionName;

    private int dataSetSize;
    private List<TabularInstance> instances;
    private AnchorConstructionBuilder<TabularInstance> constructionBuilder;
    private AnchorTabular anchorTabular;
    private H2oTabularMojoClassifier classificationFunction;
    private AnchorTabular.TabularPreprocessorBuilder tabularPreprocessor;

    public AnchorProcessor(String connectionName, H2oApi api, ModelBO modelBO, FrameBO frameBO, Map<String, Object> anchorConfig, final String modelId, final String frameId) {
        this.connectionName = connectionName;
        this.api = api;
        this.modelBO = modelBO;
        this.frameBO = frameBO;
        this.anchorConfig = anchorConfig;
        this.modelId = modelId;
        this.frameId = frameId;
    }

    public void preProcess(FrameInstance instance) throws DataAccessException {
        final int bucketNo = AnchorConfig.getBucketNo(anchorConfig);

        final List<String[]> dataSet = new LinkedList<>();
        final Map<String, Integer> header = H2oDataUtil.loadDataSetFromH2o(frameId, this.api, dataSet);

        this.tabularPreprocessor = buildAnchorPreprocessor(this.connectionName, this.modelId, this.frameId, header,
                dataSet, bucketNo);
        this.anchorTabular = this.tabularPreprocessor.build(dataSet);

        this.dataSetSize = dataSet.size();
        // garbage!
        dataSet.clear();

        this.instances = anchorTabular.getTabularInstances();

        String targetFeatureName = anchorTabular.getMappings().keySet().stream().filter((TabularFeature::isTargetFeature)).findFirst().orElseThrow(() -> new IllegalStateException("no target column found")).getName();
        List<String> sortedHeaderNames = this.instances.get(0).getFeatureNamesMapping().entrySet().stream().filter((entry) -> !targetFeatureName.equals(entry.getKey())).sorted(Comparator.comparingInt(Map.Entry::getValue)).map(Map.Entry::getKey).collect(Collectors.toList());
        this.classificationFunction = H2oTabularMojoClassifier.create(this.api, this.modelId, sortedHeaderNames);
        final TabularInstance convertedInstance = new TabularInstance(instance.getFeatureNamesMapping(), null, instance.getInstance(), instance.getInstance());
        final TabularInstance cleanedInstance = AnchorUtil.handleInstanceToExplain(convertedInstance, tabularPreprocessor, anchorTabular);

        this.constructionBuilder = createAnchorBuilderWithConfig(this.anchorTabular, this.classificationFunction,
                cleanedInstance, this.anchorConfig);
    }

    public Anchor singleExplanation() {
        return AnchorUtil.transformAnchor(
                this.modelId,
                this.frameId,
                this.getDataSetSize(),
                this.tabularPreprocessor,
                this.anchorTabular,
                this.classificationFunction,
                this.getConstructionBuilder().build().constructAnchor());
    }

    public SubmodularPickResult globalExplanation(AbstractGlobalExplainer<TabularInstance> globalExplainer) {
        final int noAnchor = AnchorConfig.getSpAnchorNo(anchorConfig);
        List<AnchorResult<TabularInstance>> anchorResults = globalExplainer.run(this.getInstances(), noAnchor);

        final Collection<Anchor> explanations = new ArrayList<>(anchorResults.size());
        anchorResults.forEach((anchorResult) -> explanations.add(AnchorUtil.transformAnchor(modelId, frameId,
                this.getDataSetSize(), this.getTabularPreprocessor(), this.getAnchorTabular(),
                this.getClassificationFunction(), anchorResult)));

        AnchorUtil.calculateCoveragePerPredicate(this.getInstances(), explanations);

        final Set<TabularInstance> globalCoverageInstances = AnchorUtil.computeGlobalCoverage(
                this.getInstances(),
                anchorResults
        );
        final double aggregatedCoverage = globalCoverageInstances.size() / (double) this.getDataSetSize();
        return new SubmodularPickResult(explanations, aggregatedCoverage);
    }

    private AnchorTabular.TabularPreprocessorBuilder buildAnchorPreprocessor(String connectionName,
                                                                             String modelId,
                                                                             String frameId,
                                                                             Map<String, Integer> header,
                                                                             List<String[]> dataSet,
                                                                             final int classCount) throws DataAccessException {
        Model model = this.modelBO.getModel(connectionName, modelId);
        FrameSummary frameSummary = this.frameBO.getFrameSummary(connectionName, frameId);

        AnchorTabular.TabularPreprocessorBuilder anchorBuilder = new AnchorTabular.TabularPreprocessorBuilder();
        AnchorUtil.addColumnsToAnchorBuilder(
                anchorBuilder,
                header,
                model.getTarget_column(),
                frameSummary.getColumn_summary_list(),
                model.getIgnoredColumns(),
                classCount
        );

        AnchorUtil.handleNa(header, dataSet, anchorBuilder.getColumnDescriptions());

        return anchorBuilder;
    }

    private AnchorConstructionBuilder<TabularInstance> createAnchorBuilderWithConfig(AnchorTabular anchorTabular,
                                                                                     ClassificationFunction<TabularInstance> classificationFunction,
                                                                                     TabularInstance cleanedInstance,
                                                                                     Map<String, Object> anchorConfig) {
        ReconfigurablePerturbationFunction<TabularInstance> tabularPerturbationFunction = new TabularWithOriginalDataPerturbationFunction(
                cleanedInstance,
                anchorTabular.getTabularInstances().toArray(new TabularInstance[0]));

        final double anchorTau = AnchorConfig.getTau(anchorConfig);
        final double anchorDelta = 0.1; // (Double) AnchorUtil.getAnchorOptionFromParamsOrDefault(anchorConfig, ANCHOR_DELTA);
        final double anchorEpsilon = 0.1; // (Double) AnchorUtil.getAnchorOptionFromParamsOrDefault(anchorConfig, ANCHOR_EPSILON);
        final double anchorTauDiscrepancy = AnchorConfig.getTauDiscrepancy(anchorConfig);

        return new AnchorConstructionBuilder<>(classificationFunction,
                tabularPerturbationFunction, cleanedInstance, classificationFunction.predict(cleanedInstance))
                .setBestAnchorIdentification(new BatchSAR(100 * anchorTabular.getFeatures().size(), 10))
                .setInitSampleCount(100)
                .setTau(anchorTau)
                .setDelta(anchorDelta)
                .setEpsilon(anchorEpsilon)
                .setTauDiscrepancy(anchorTauDiscrepancy)
                .setAllowSuboptimalSteps(false);
    }

    public List<TabularInstance> getInstances() {
        return this.instances;
    }

    public AnchorConstructionBuilder<TabularInstance> getConstructionBuilder() {
        return this.constructionBuilder;
    }

    public int getDataSetSize() {
        return this.dataSetSize;
    }

    public AnchorTabular getAnchorTabular() {
        return this.anchorTabular;
    }

    public H2oTabularMojoClassifier getClassificationFunction() {
        return this.classificationFunction;
    }

    public AnchorTabular.TabularPreprocessorBuilder getTabularPreprocessor() {
        return tabularPreprocessor;
    }
}
