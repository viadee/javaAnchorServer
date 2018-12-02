package de.viadee.anchorj.server.anchor.h2o.spark;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.Map;

import org.apache.commons.lang3.NotImplementedException;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaSparkContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import de.viadee.anchorj.global.AbstractGlobalExplainer;
import de.viadee.anchorj.global.CoveragePick;
import de.viadee.anchorj.server.anchor.AnchorRule;
import de.viadee.anchorj.server.anchor.util.AnchorConfig;
import de.viadee.anchorj.server.anchor.util.AnchorProcessor;
import de.viadee.anchorj.server.api.exceptions.DataAccessException;
import de.viadee.anchorj.server.business.FrameBO;
import de.viadee.anchorj.server.business.ModelBO;
import de.viadee.anchorj.server.h2o.util.H2oConnector;
import de.viadee.anchorj.server.model.Anchor;
import de.viadee.anchorj.server.model.AnchorConfigDescription;
import de.viadee.anchorj.server.model.FrameInstance;
import de.viadee.anchorj.server.model.SubmodularPickResult;
import de.viadee.anchorj.spark.SparkBatchExplainer;
import de.viadee.anchorj.tabular.TabularInstance;
import water.bindings.H2oApi;

/**
 *
 */
@Component("spark")
public class AnchorSpark implements AnchorRule, H2oConnector {

    private static final Logger LOG = LoggerFactory.getLogger(AnchorSpark.class);

    private static final String SPARK_LIB_FOLDER = "/Users/akr/git/javaAnchorServer/application/target/libs";

    private ModelBO modelBO;

    private FrameBO frameBO;

    public AnchorSpark(@Autowired ModelBO modelBO, @Autowired FrameBO frameBO) {
        this.modelBO = modelBO;
        this.frameBO = frameBO;
    }

    @Override
    public SubmodularPickResult runSubmodularPick(String connectionName, String modelId, String frameId, FrameInstance instance, Map<String, Object> anchorConfig) throws DataAccessException {
        final H2oApi api = this.createH2o(connectionName);
        AnchorProcessor processor = new AnchorProcessor(connectionName, api, modelBO, frameBO, anchorConfig, modelId,
                frameId);
        processor.preProcess(instance);


        final SparkConf sparkConf;
        final String sparkMasterUrl = "spark://localhost:7077";
        try {
            sparkConf = new SparkConf().setAppName("anchorj").setMaster(sparkMasterUrl)
                    //                .s
                    .set("spark.shuffle.service.enabled", "false")
                    .set("spark.dynamicAllocation.enabled", "false")
                    //                .set("spark.io.compression.codec", "snappy")
                    .setJars(Files.list(Paths.get(SPARK_LIB_FOLDER)).map(Path::toFile).map(File::getAbsolutePath).toArray(String[]::new))
                    .set("spark.rdd.compress", "true");
        } catch (IOException e) {
            throw new DataAccessException("Failed to connect to the Spark Master: " + sparkMasterUrl, e);
        }

        try (JavaSparkContext sc = new JavaSparkContext(sparkConf)) {
            SparkBatchExplainer<TabularInstance> explainer = new SparkBatchExplainer<>(sc);
            final AbstractGlobalExplainer<TabularInstance> subPick = new CoveragePick<>(explainer, processor.getConstructionBuilder());
            return processor.globalExplanation(subPick);
        } catch (Exception e) {
            throw new DataAccessException("Failed to run Submodular Pick: " + e.getMessage(), e);
        }
    }

    @Override
    public Anchor computeRule(String connectionName, String modelId, String frameId, FrameInstance instance, Map<String, Object> anchorConfig) throws DataAccessException {
        throw new NotImplementedException("not available for spark");
    }

    @Override
    public Collection<AnchorConfigDescription> getAnchorConfigs() {
        return AnchorConfig.getAnchorConfigs();
    }
}
