package de.viadee.anchorj.server.anchor;

import java.util.Collection;
import java.util.Map;

import de.viadee.anchorj.server.api.exceptions.DataAccessException;
import de.viadee.anchorj.server.model.Anchor;
import de.viadee.anchorj.server.model.AnchorConfigDescription;
import de.viadee.anchorj.server.model.FrameInstance;
import de.viadee.anchorj.server.model.SubmodularPickResult;

public interface AnchorRule {

    SubmodularPickResult runSubmodularPick(String connectionName,
                                           String modelId,
                                           String frameId,
                                           FrameInstance instance,
                                           Map<String, Object> anchorConfig) throws DataAccessException;

    Anchor computeRule(String connectionName,
                       String modelId,
                       String frameId,
                       FrameInstance instance,
                       Map<String, Object> anchorConfig,
                       Long seed) throws DataAccessException;

    Collection<AnchorConfigDescription> getAnchorConfigs() throws DataAccessException;

}
