package de.viadee.anchorj.server.anchor.h2o.spark;

import java.util.Collection;
import java.util.Map;

import org.apache.commons.lang3.NotImplementedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import de.viadee.anchorj.server.anchor.AnchorRule;
import de.viadee.anchorj.server.anchor.util.AnchorConfig;
import de.viadee.anchorj.server.anchor.util.AnchorProcessor;
import de.viadee.anchorj.server.api.exceptions.DataAccessException;
import de.viadee.anchorj.server.business.FrameBO;
import de.viadee.anchorj.server.business.ModelBO;
import de.viadee.anchorj.server.configuration.AppConfiguration;
import de.viadee.anchorj.server.h2o.util.H2oConnector;
import de.viadee.anchorj.server.model.Anchor;
import de.viadee.anchorj.server.model.AnchorConfigDescription;
import de.viadee.anchorj.server.model.FrameInstance;
import de.viadee.anchorj.server.model.SubmodularPickResult;
import de.viadee.xai.anchor.adapter.global.SparkBatchExplainer;
import de.viadee.xai.anchor.adapter.tabular.TabularInstance;
import de.viadee.xai.anchor.algorithm.global.AbstractGlobalExplainer;
import de.viadee.xai.anchor.algorithm.global.CoveragePick;
import water.bindings.H2oApi;

/**
 *
 */
@Component("spark")
public class AnchorSpark implements AnchorRule, H2oConnector {

    private ModelBO modelBO;
    private FrameBO frameBO;
    private SparkConfiguration sparkConf;
    private AppConfiguration configuration;

    public AnchorSpark(@Autowired ModelBO modelBO, @Autowired FrameBO frameBO, @Autowired SparkConfiguration sparkConf,
                       @Autowired AppConfiguration configuration) {
        this.modelBO = modelBO;
        this.frameBO = frameBO;
        this.sparkConf = sparkConf;
        this.configuration = configuration;
    }

    @Override
    public SubmodularPickResult runSubmodularPick(String connectionName, String modelId, String frameId, FrameInstance instance, Map<String, Object> anchorConfig) throws DataAccessException {
        final H2oApi api = this.createH2o(this.configuration, connectionName);
        AnchorProcessor processor = new AnchorProcessor(connectionName, api, this.modelBO, this.frameBO, anchorConfig,
                modelId, frameId);
        processor.preProcess(instance);
        SparkBatchExplainer<TabularInstance> explainer = new SparkBatchExplainer<>(this.sparkConf.getSparkContext());
        final AbstractGlobalExplainer<TabularInstance> subPick = new CoveragePick<>(false, explainer, processor.getConstructionBuilder());

        return processor.globalExplanation(subPick);
    }

    @Override
    public Anchor computeRule(String connectionName, String modelId, String frameId, FrameInstance instance,
                              Map<String, Object> anchorConfig, Long seed) throws DataAccessException {
        throw new NotImplementedException("not available for spark");
    }

    @Override
    public Collection<AnchorConfigDescription> getAnchorConfigs() {
        return AnchorConfig.getAnchorConfigs();
    }
}
