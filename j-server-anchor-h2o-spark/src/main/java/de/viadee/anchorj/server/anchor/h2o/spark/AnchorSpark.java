package de.viadee.anchorj.server.anchor.h2o.spark;

import java.util.Collection;
import java.util.Map;

import me.kroeker.alex.anchor.jserver.anchor.AnchorRule;
import me.kroeker.alex.anchor.jserver.api.exceptions.DataAccessException;
import me.kroeker.alex.anchor.jserver.model.Anchor;
import me.kroeker.alex.anchor.jserver.model.AnchorConfigDescription;
import me.kroeker.alex.anchor.jserver.model.FrameInstance;
import me.kroeker.alex.anchor.jserver.model.SubmodularPickResult;

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
