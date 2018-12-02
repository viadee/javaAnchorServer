package de.viadee.anchorj.server.anchor.h2o.spark;

import java.util.Collection;
import java.util.Map;

import de.viadee.anchorj.server.anchor.AnchorRule;
import de.viadee.anchorj.server.api.exceptions.DataAccessException;
import de.viadee.anchorj.server.model.Anchor;
import de.viadee.anchorj.server.model.AnchorConfigDescription;
import de.viadee.anchorj.server.model.FrameInstance;
import de.viadee.anchorj.server.model.SubmodularPickResult;

/**
 *
 */
public class AnchorSpark implements AnchorRule {

    @Override
    public SubmodularPickResult runSubmodularPick(String connectionName, String modelId, String frameId, FrameInstance instance, Map<String, Object> anchorConfig) throws DataAccessException {
        return null;
    }

    @Override
    public Anchor computeRule(String connectionName, String modelId, String frameId, FrameInstance instance, Map<String, Object> anchorConfig) throws DataAccessException {
        return null;
    }

    @Override
    public Collection<AnchorConfigDescription> getAnchorConfigs() throws DataAccessException {
        return null;
    }
}
