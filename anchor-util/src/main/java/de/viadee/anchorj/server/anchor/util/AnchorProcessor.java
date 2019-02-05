package de.viadee.anchorj.server.anchor.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import de.viadee.anchorj.server.api.exceptions.DataAccessException;
import de.viadee.anchorj.server.business.FrameBO;
import de.viadee.anchorj.server.business.ModelBO;
import de.viadee.anchorj.server.h2o.util.H2oDataUtil;
import de.viadee.anchorj.server.h2o.util.H2oDownload;
import de.viadee.anchorj.server.h2o.util.H2oMojoDownload;
import de.viadee.anchorj.server.model.Anchor;
import de.viadee.anchorj.server.model.FrameInstance;
import de.viadee.anchorj.server.model.FrameSummary;
import de.viadee.anchorj.server.model.Model;
import de.viadee.anchorj.server.model.SubmodularPickResult;
import de.viadee.xai.anchor.adapter.model.h2o.H2oTabularNominalMojoClassifier;
import de.viadee.xai.anchor.adapter.tabular.AnchorTabular;
import de.viadee.xai.anchor.adapter.tabular.TabularInstance;
import de.viadee.xai.anchor.adapter.tabular.TabularPerturbationFunction;
import de.viadee.xai.anchor.adapter.tabular.column.GenericColumn;
import de.viadee.xai.anchor.algorithm.AnchorConstructionBuilder;
import de.viadee.xai.anchor.algorithm.exploration.KL_LUCB;
import de.viadee.xai.anchor.algorithm.global.AbstractGlobalExplainer;
import de.viadee.xai.anchor.algorithm.global.ReconfigurablePerturbationFunction;
import water.bindings.H2oApi;

/**
 *
 */
@SuppressWarnings({ "WeakerAccess", "ClassWithTooManyFields" })
public class AnchorProcessor {

    private final H2oApi api;
    private final ModelBO modelBO;
    private final FrameBO frameBO;
    private final Map<String, Object> anchorConfig;
    private final String modelId;
    private final String frameId;
    private final String connectionName;

    private int dataSetSize;
    private AnchorConstructionBuilder<TabularInstance> constructionBuilder;
    private AnchorTabular anchorTabular;
    private H2oTabularNominalMojoClassifier<TabularInstance> classificationFunction;
    private AnchorTabular.Builder tabularPreprocessor;
    private Long seed;

    public AnchorProcessor(String connectionName, H2oApi api, ModelBO modelBO, FrameBO frameBO, Map<String, Object> anchorConfig, final String modelId, final String frameId) {
        this(connectionName, api, modelBO, frameBO, anchorConfig, modelId, frameId, null);
    }

    public AnchorProcessor(String connectionName, H2oApi api, ModelBO modelBO, FrameBO frameBO, Map<String, Object> anchorConfig, final String modelId, final String frameId, Long seed) {
        this.connectionName = connectionName;
        this.api = api;
        this.modelBO = modelBO;
        this.frameBO = frameBO;
        this.anchorConfig = anchorConfig;
        this.modelId = modelId;
        this.frameId = frameId;
        this.seed = seed;
    }

    public void preProcess(FrameInstance instance) throws DataAccessException {
        final int bucketNo = AnchorConfig.getBucketNo(anchorConfig);

        final List<String[]> dataSet = new LinkedList<>();
        final Map<String, Integer> header = H2oDataUtil.loadDataSetFromH2o(frameId, this.api, dataSet);

        this.tabularPreprocessor = buildAnchorPreprocessor(this.connectionName, this.modelId, this.frameId, header, bucketNo);
        this.anchorTabular = this.tabularPreprocessor.build(dataSet);

        this.dataSetSize = dataSet.size();
        // garbage!
        dataSet.clear();

        List<String> sortedHeaderNames = anchorTabular.getColumns().stream().map(GenericColumn::getName)
                .collect(Collectors.toList());
        this.classificationFunction = createMojoClassifier(this.api, this.modelId, sortedHeaderNames);

        final TabularInstance cleanedInstance = AnchorTabular.preprocessData(anchorTabular,
                Collections.singleton(instance.getInstance()), false)[0];

        this.constructionBuilder = createAnchorBuilderWithConfig(cleanedInstance, this.seed);
    }

    public static H2oTabularNominalMojoClassifier<TabularInstance> createMojoClassifier(final H2oApi api, String modelId, List<String> sortedHeaderMapping)
            throws DataAccessException {

        H2oTabularNominalMojoClassifier<TabularInstance> classificationFunction;
        try (H2oDownload mojoDownload = new H2oMojoDownload()) {
            File mojoFile = mojoDownload.getFile(api, modelId);

            classificationFunction = new H2oTabularNominalMojoClassifier<>(
                    new FileInputStream(mojoFile),
                    sortedHeaderMapping);
        } catch (IOException e) {
            throw new DataAccessException("Failed to load Model MOJO with id: " + modelId + " and connection: " + api.getUrl(), e);
        }
        return classificationFunction;
    }

    public Anchor singleExplanation() {
        AnchorResultWithExactCoverage result = new AnchorResultWithExactCoverage(this.getConstructionBuilder().build().constructAnchor());
        computeSingleAnchorCoverage(result);

        return AnchorUtil.transformAnchor(
                this.modelId,
                this.frameId,
                this.getDataSetSize(),
                this.anchorTabular,
                this.classificationFunction,
                result);
    }

    private void computeSingleAnchorCoverage(AnchorResultWithExactCoverage result) {
        final Set<TabularInstance> coveredInstances = AnchorUtil.findCoveredInstances(
                this.getInstances(),
                Collections.singletonList(result)
        );
        final double aggregatedCoverage = coveredInstances.size() / (double) this.getDataSetSize();
        result.setExactCoverage(aggregatedCoverage);
    }

    public SubmodularPickResult globalExplanation(AbstractGlobalExplainer<TabularInstance> globalExplainer) {
        final int noAnchor = AnchorConfig.getSpAnchorNo(this.anchorConfig);
        List<AnchorResultWithExactCoverage> anchorResults = globalExplainer.run(this.getInstances(), noAnchor)
                .stream().map(AnchorResultWithExactCoverage::new).collect(Collectors.toList());
        anchorResults.forEach(this::computeSingleAnchorCoverage);

        final Collection<Anchor> explanations = new ArrayList<>(anchorResults.size());
        for (AnchorResultWithExactCoverage anchorResult : anchorResults) {
            Anchor anchor = AnchorUtil.transformAnchor(this.modelId, this.frameId,
                    this.getDataSetSize(), this.getAnchorTabular(),
                    this.getClassificationFunction(), anchorResult);

            explanations.add(anchor);
        }

        AnchorUtil.calculateCoveragePerPredicate(this.getInstances(), explanations);

        final Set<TabularInstance> globalCoverageInstances = AnchorUtil.findCoveredInstances(
                this.getInstances(),
                anchorResults
        );
        final double aggregatedCoverage = globalCoverageInstances.size() / (double) this.getDataSetSize();
        return new SubmodularPickResult(explanations, aggregatedCoverage);
    }

    private AnchorTabular.Builder buildAnchorPreprocessor(String connectionName,
                                                          String modelId,
                                                          String frameId,
                                                          Map<String, Integer> header,
                                                          final int classCount) throws DataAccessException {
        Model model = this.modelBO.getModel(connectionName, modelId);
        FrameSummary frameSummary = this.frameBO.getFrameSummary(connectionName, frameId);

        AnchorTabular.Builder anchorBuilder = new AnchorTabular.Builder();
        AnchorUtil.addColumnsToAnchorBuilder(
                anchorBuilder,
                header,
                model.getTarget_column(),
                frameSummary.getColumn_summary_list(),
                model.getIgnoredColumns(),
                classCount
        );

        return anchorBuilder;
    }

    private AnchorConstructionBuilder<TabularInstance> createAnchorBuilderWithConfig(TabularInstance cleanedInstance, Long seed) {
        ReconfigurablePerturbationFunction<TabularInstance> tabularPerturbationFunction = new TabularPerturbationFunction(
                cleanedInstance,
                anchorTabular.getTabularInstances(),
                seed);

        final double anchorTau = AnchorConfig.getTau(anchorConfig);
        final double anchorDelta = 0.1; // (Double) AnchorUtil.getAnchorOptionFromParamsOrDefault(anchorConfig, ANCHOR_DELTA);
        final double anchorEpsilon = 0.1; // (Double) AnchorUtil.getAnchorOptionFromParamsOrDefault(anchorConfig, ANCHOR_EPSILON);
        final double anchorTauDiscrepancy = AnchorConfig.getTauDiscrepancy(anchorConfig);
        final int sampleSize = AnchorConfig.getSampleSize(anchorConfig);

        return new AnchorConstructionBuilder<>(classificationFunction,
                tabularPerturbationFunction, cleanedInstance, classificationFunction.predict(cleanedInstance))
                .setBestAnchorIdentification(new KL_LUCB(100))
                .setInitSampleCount(sampleSize)
                .setTau(anchorTau)
                .setDelta(anchorDelta)
                .setEpsilon(anchorEpsilon)
                .setTauDiscrepancy(anchorTauDiscrepancy)
                .enableThreading(Executors.newCachedThreadPool(), Executors::newCachedThreadPool)
                .setAllowSuboptimalSteps(false);
    }

    public TabularInstance[] getInstances() {
        return this.anchorTabular.getTabularInstances();
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

    public H2oTabularNominalMojoClassifier<TabularInstance> getClassificationFunction() {
        return this.classificationFunction;
    }

    @SuppressWarnings("unused")
    public AnchorTabular.Builder getTabularPreprocessor() {
        return tabularPreprocessor;
    }
}
