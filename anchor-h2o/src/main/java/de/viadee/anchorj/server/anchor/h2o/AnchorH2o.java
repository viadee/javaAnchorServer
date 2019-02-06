package de.viadee.anchorj.server.anchor.h2o;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.Executors;

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
import de.viadee.xai.anchor.adapter.tabular.TabularInstance;
import de.viadee.xai.anchor.algorithm.execution.ExecutorServiceSupplier;
import de.viadee.xai.anchor.algorithm.global.AbstractGlobalExplainer;
import de.viadee.xai.anchor.algorithm.global.BatchExplainer;
import de.viadee.xai.anchor.algorithm.global.CoveragePick;
import de.viadee.xai.anchor.algorithm.global.ThreadedBatchExplainer;
import water.bindings.H2oApi;

@Component("local")
public class AnchorH2o implements AnchorRule, H2oConnector {

    private static final int SP_MAX_THREADS = 10;

    private ModelBO modelBO;
    private FrameBO frameBO;
    private AppConfiguration configuration;

    public AnchorH2o(@Autowired ModelBO modelBO, @Autowired FrameBO frameBO, @Autowired AppConfiguration configuration) {
        this.modelBO = modelBO;
        this.frameBO = frameBO;
        this.configuration = configuration;
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
        final H2oApi api = this.createH2o(this.configuration, connectionName);
        AnchorProcessor processor = new AnchorProcessor(connectionName, api, this.modelBO, this.frameBO, anchorConfig,
                modelId, frameId);
        BatchExplainer<TabularInstance> explainer = new ThreadedBatchExplainer<>(SP_MAX_THREADS,
                Executors.newCachedThreadPool(), (ExecutorServiceSupplier) Executors::newCachedThreadPool);
        processor.preProcess(instance);
        final AbstractGlobalExplainer<TabularInstance> subPick = new CoveragePick<>(false, explainer,
                processor.getConstructionBuilder());

        return processor.globalExplanation(subPick);
    }

    @Override
    public Anchor computeRule(String connectionName, String modelId, String frameId, FrameInstance instance,
                              Map<String, Object> anchorConfig, Long seed)
            throws DataAccessException {
        final H2oApi api = this.createH2o(this.configuration, connectionName);
        AnchorProcessor processor = new AnchorProcessor(connectionName, api, this.modelBO, this.frameBO, anchorConfig,
                modelId, frameId, seed);
        processor.preProcess(instance);

        return processor.singleExplanation();
    }

}
