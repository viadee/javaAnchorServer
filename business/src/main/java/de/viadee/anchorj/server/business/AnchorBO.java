package de.viadee.anchorj.server.business;

import java.util.Collection;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import de.viadee.anchorj.server.anchor.AnchorRule;
import de.viadee.anchorj.server.api.exceptions.DataAccessException;
import de.viadee.anchorj.server.model.Anchor;
import de.viadee.anchorj.server.model.AnchorConfigDescription;
import de.viadee.anchorj.server.model.FrameInstance;
import de.viadee.anchorj.server.model.SubmodularPickResult;

@Component
public class AnchorBO {

    private AnchorRule anchorRule;

    public AnchorBO(@Autowired AnchorRule anchorRule) {
        this.anchorRule = anchorRule;
    }

    public Anchor computeRule(String connectionName,
                              String modelId,
                              String frameId,
                              FrameInstance instance,
                              Map<String, Object> anchorConfig) throws DataAccessException {
        return this.anchorRule.computeRule(connectionName, modelId, frameId, instance, anchorConfig, null);
    }

    public SubmodularPickResult runSubmodularPick(String connectionName,
                                                  String modelId,
                                                  String frameId,
                                                  FrameInstance instance,
                                                  Map<String, Object> anchorConfig) throws DataAccessException {
        return this.anchorRule.runSubmodularPick(connectionName, modelId, frameId, instance, anchorConfig);
    }

    public Collection<AnchorConfigDescription> getAnchorConfigs() throws DataAccessException {
        return this.anchorRule.getAnchorConfigs();
    }

}
