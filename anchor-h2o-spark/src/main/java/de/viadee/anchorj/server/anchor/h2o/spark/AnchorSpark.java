package de.viadee.anchorj.server.anchor.h2o.spark;

import java.util.Collection;
import java.util.Map;

import org.apache.commons.lang3.NotImplementedException;
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

    private ModelBO modelBO;

    private FrameBO frameBO;

    private SparkCon sparkCon;

    public AnchorSpark(@Autowired ModelBO modelBO, @Autowired FrameBO frameBO, @Autowired SparkCon sparkCon) {
        this.modelBO = modelBO;
        this.frameBO = frameBO;
        this.sparkCon = sparkCon;
    }

    @Override
    public SubmodularPickResult runSubmodularPick(String connectionName, String modelId, String frameId, FrameInstance instance, Map<String, Object> anchorConfig) throws DataAccessException {
        final H2oApi api = this.createH2o(connectionName);
        AnchorProcessor processor = new AnchorProcessor(connectionName, api, modelBO, frameBO, anchorConfig, modelId,
                frameId);
        processor.preProcess(instance);
        SparkBatchExplainer<TabularInstance> explainer = new SparkBatchExplainer<>(this.sparkCon.getSparkContext());
        final AbstractGlobalExplainer<TabularInstance> subPick = new CoveragePick<>(explainer, processor.getConstructionBuilder());

        return processor.globalExplanation(subPick);
    }

    @Override
    public Anchor computeRule(String connectionName, String modelId, String frameId, FrameInstance instance, Map<String, Object> anchorConfig, Long seed) throws DataAccessException {
        throw new NotImplementedException("not available for spark");
    }

    @Override
    public Collection<AnchorConfigDescription> getAnchorConfigs() {
        return AnchorConfig.getAnchorConfigs();
    }
}
