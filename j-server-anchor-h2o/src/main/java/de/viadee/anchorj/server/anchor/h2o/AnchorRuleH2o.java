package de.viadee.anchorj.server.anchor.h2o;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaSparkContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import de.viadee.anchorj.AnchorConstructionBuilder;
import de.viadee.anchorj.AnchorResult;
import de.viadee.anchorj.server.anchor.util.AnchorConfig;
import de.viadee.anchorj.server.anchor.util.AnchorUtil;
import de.viadee.anchorj.server.anchor.util.H2oTabularMojoClassifier;
import de.viadee.anchorj.server.anchor.util.TabularWithOriginalDataPerturbationFunction;
import de.viadee.anchorj.exploration.BatchSAR;
import de.viadee.anchorj.global.AbstractGlobalExplainer;
import de.viadee.anchorj.global.CoveragePick;
import de.viadee.anchorj.global.ReconfigurablePerturbationFunction;
import de.viadee.anchorj.spark.SparkBatchExplainer;
import de.viadee.anchorj.tabular.AnchorTabular;
import de.viadee.anchorj.tabular.TabularFeature;
import de.viadee.anchorj.tabular.TabularInstance;
import de.viadee.anchorj.server.anchor.AnchorRule;
import de.viadee.anchorj.server.api.exceptions.DataAccessException;
import de.viadee.anchorj.server.business.FrameBO;
import de.viadee.anchorj.server.business.ModelBO;
import de.viadee.anchorj.server.h2o.util.H2oConnector;
import de.viadee.anchorj.server.h2o.util.H2oDataUtil;
import de.viadee.anchorj.server.h2o.util.H2oDownload;
import de.viadee.anchorj.server.h2o.util.H2oMojoDownload;
import de.viadee.anchorj.server.model.Anchor;
import de.viadee.anchorj.server.model.AnchorConfigDescription;
import de.viadee.anchorj.server.model.AnchorPredicate;
import de.viadee.anchorj.server.model.FrameInstance;
import de.viadee.anchorj.server.model.FrameSummary;
import de.viadee.anchorj.server.model.Model;
import de.viadee.anchorj.server.model.SubmodularPickResult;
import water.bindings.H2oApi;

@Component
public class AnchorRuleH2o implements AnchorRule, H2oConnector {
//    private static final long serialVersionUID = 1315158080441804288L;

    private static final Logger LOG = LoggerFactory.getLogger(AnchorRuleH2o.class);

    private ModelBO modelBO;

    private FrameBO frameBO;

    public AnchorRuleH2o(@Autowired ModelBO modelBO, @Autowired FrameBO frameBO) {
        this.modelBO = modelBO;
        this.frameBO = frameBO;
    }

    @Override
    public Collection<AnchorConfigDescription> getAnchorConfigs() {
        return AnchorConfig.getAnchorConfigs();
    }

    @Override
    public SubmodularPickResult runSubmodularPick(String connectionName,
                                                  String modelId,
                                                  String frameId,
                                                  FrameInstance instance,
                                                  Map<String, Object> anchorConfig) throws DataAccessException {
        final H2oApi api = this.createH2o(connectionName);
        final List<String[]> dataSet = new LinkedList<>();
        final Map<String, Integer> header = H2oDataUtil.loadDataSetFromH2o(frameId, api, dataSet);

        final int bucketNo = AnchorConfig.getBucketNo(anchorConfig);
        final int noAnchor = AnchorConfig.getSpAnchorNo(anchorConfig);

        final AnchorTabular.TabularPreprocessorBuilder anchorBuilder =
                buildAnchorPreprocessor(connectionName, modelId, frameId, header, dataSet, bucketNo);
        final AnchorTabular anchorTabular = anchorBuilder.build(dataSet);

        final int dataSetSize = dataSet.size();
        // garbage!
        dataSet.clear();

        final H2oTabularMojoClassifier classificationFunction = generateH2oClassifier(connectionName, modelId, anchorTabular);
        final TabularInstance convertedInstance = new TabularInstance(instance.getFeatureNamesMapping(), instance.getInstance());
        final TabularInstance cleanedInstance = AnchorUtil.handleInstanceToExplain(convertedInstance, anchorBuilder);

        final AnchorConstructionBuilder<TabularInstance> anchorConstructionBuilder = createAnchorBuilderWithConfig(anchorTabular, classificationFunction, cleanedInstance, anchorConfig);

        final String lobFolder = "/Users/akr/git/javaAnchorServer/j-server-application/target/libs";

        final SparkConf sparkConf;
        final String sparkMasterUrl = "spark://localhost:7077";
        try {
            sparkConf = new SparkConf().setAppName("anchorj").setMaster(sparkMasterUrl)
                    //                .s
                    .set("spark.shuffle.service.enabled", "false")
                    .set("spark.dynamicAllocation.enabled", "false")
                    //                .set("spark.io.compression.codec", "snappy")
                    .setJars(Files.list(Paths.get(lobFolder)).map(Path::toFile).map(File::getAbsolutePath).toArray(String[]::new))
                    .set("spark.rdd.compress", "true");
        } catch (IOException e) {
            throw new DataAccessException("Failed to connect to the Spark Master: " + sparkMasterUrl, e);
        }

        List<AnchorResult<TabularInstance>> anchorResults;
        try (JavaSparkContext sc = new JavaSparkContext(sparkConf)) {
            SparkBatchExplainer<TabularInstance> explainer = new SparkBatchExplainer<>(sc);
            final AbstractGlobalExplainer<TabularInstance> subPick = new CoveragePick<>(explainer, anchorConstructionBuilder);
            anchorResults = subPick.run(anchorTabular.getTabularInstances().getInstances(), noAnchor);
        } catch (Exception e) {
            throw new DataAccessException("Failed to run Submodular Pick: " + e.getMessage(), e);
        }

        final Collection<Anchor> explanations = new ArrayList<>(anchorResults.size());
        anchorResults.forEach((anchorResult) -> explanations.add(AnchorUtil.transformAnchor(modelId, frameId,
                dataSetSize, anchorBuilder, anchorTabular, classificationFunction, anchorResult)));

        final Set<TabularInstance> globalCoverageInstances = AnchorUtil.computeGlobalCoverage(
                anchorTabular.getTabularInstances(),
                anchorResults
        );


        final Map<String, Double> predicateCoverage = new HashMap<>();
        final Consumer<AnchorPredicate> calculateCoverage = predicate -> {
            final String featureName = predicate.getFeatureName();
            double exactCoverage;
            if (!predicateCoverage.containsKey(featureName)) {
                exactCoverage = AnchorUtil.computeExactCoverage(anchorTabular.getTabularInstances(), predicate);
                predicateCoverage.put(featureName, exactCoverage);
            } else {
                exactCoverage = predicateCoverage.get(featureName);
            }
            predicate.setExactCoverage(exactCoverage);
        };
        explanations.forEach((expl) -> {
            expl.getEnumPredicate().values().forEach(calculateCoverage);
            expl.getMetricPredicate().values().forEach(calculateCoverage);
        });

        final double aggregatedCoverage = globalCoverageInstances.size() / (double) dataSetSize;
        return new SubmodularPickResult(explanations, aggregatedCoverage);
    }

    @Override
    public Anchor computeRule(String connectionName, String modelId, String frameId, FrameInstance instance,
                              Map<String, Object> anchorConfig)
            throws DataAccessException {
        H2oApi api = this.createH2o(connectionName);
        final List<String[]> dataSet = new LinkedList<>();
        Map<String, Integer> header = H2oDataUtil.loadDataSetFromH2o(frameId, api, dataSet);

        final int bucketNo = AnchorConfig.getBucketNo(anchorConfig);

        AnchorTabular.TabularPreprocessorBuilder anchorBuilder = buildAnchorPreprocessor(connectionName, modelId,
                frameId, header, dataSet, bucketNo);
        AnchorTabular anchorTabular = anchorBuilder.build(dataSet);
        final int dataSetSize = dataSet.size();
        // garbage!
        dataSet.clear();

        TabularInstance convertedInstance = new TabularInstance(instance.getFeatureNamesMapping(), instance.getInstance(), instance.getInstance());
        TabularInstance cleanedInstance = AnchorUtil.handleInstanceToExplain(convertedInstance, anchorBuilder);

        H2oTabularMojoClassifier classificationFunction = generateH2oClassifier(connectionName, modelId, anchorTabular);

        final AnchorResult<TabularInstance> anchorResult =
                this.createAnchorBuilderWithConfig(anchorTabular, classificationFunction, cleanedInstance, anchorConfig)
                        .build().constructAnchor();

        return AnchorUtil.transformAnchor(modelId, frameId, dataSetSize, anchorBuilder, anchorTabular,
                classificationFunction, anchorResult);
    }

    private AnchorConstructionBuilder<TabularInstance> createAnchorBuilderWithConfig(AnchorTabular anchorTabular, H2oTabularMojoClassifier classificationFunction, TabularInstance cleanedInstance, Map<String, Object> anchorConfig) {
        ReconfigurablePerturbationFunction<TabularInstance> tabularPerturbationFunction = new TabularWithOriginalDataPerturbationFunction(cleanedInstance,
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
                .setAllowSuboptimalSteps(true);
    }

    private H2oTabularMojoClassifier generateH2oClassifier(String connectionName, String modelId, AnchorTabular anchor) throws DataAccessException {
        H2oTabularMojoClassifier classificationFunction;
        try (H2oDownload mojoDownload = new H2oMojoDownload()) {
            File mojoFile = mojoDownload.getFile(this.createH2o(connectionName), modelId);

            classificationFunction = new H2oTabularMojoClassifier(
                    new FileInputStream(mojoFile),
                    AnchorUtil.generateH2oPredictor(),
                    anchor.getFeatures().stream().map(TabularFeature::getName).collect(Collectors.toList()));
        } catch (IOException e) {
            throw new DataAccessException("Failed to load Model MOJO with id: " + modelId + " and connection: " + connectionName);
        }
        return classificationFunction;
    }

    private AnchorTabular.TabularPreprocessorBuilder buildAnchorPreprocessor(String connectionName,
                                                                             String modelId,
                                                                             String frameId,
                                                                             Map<String, Integer> header,
                                                                             List<String[]> dataSet,
                                                                             final int classCount) throws DataAccessException {
        Model model = this.modelBO.getModel(connectionName, modelId);
        FrameSummary frameSummary = this.frameBO.getFrameSummary(connectionName, frameId);

        AnchorTabular.TabularPreprocessorBuilder anchorBuilder =
                new AnchorTabular.TabularPreprocessorBuilder();

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

}
