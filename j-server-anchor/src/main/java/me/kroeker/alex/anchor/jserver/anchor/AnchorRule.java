package me.kroeker.alex.anchor.jserver.anchor;

import java.util.Collection;
import java.util.Map;

import me.kroeker.alex.anchor.jserver.api.exceptions.DataAccessException;
import me.kroeker.alex.anchor.jserver.model.Anchor;
import me.kroeker.alex.anchor.jserver.model.AnchorConfigDescription;
import me.kroeker.alex.anchor.jserver.model.FrameInstance;
import me.kroeker.alex.anchor.jserver.model.SubmodularPickResult;

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
                       Map<String, Object> anchorConfig) throws DataAccessException;

    Collection<AnchorConfigDescription> getAnchorConfigs() throws DataAccessException;

}
