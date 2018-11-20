package me.kroeker.alex.anchor.jserver.anchor;

import me.kroeker.alex.anchor.jserver.api.exceptions.DataAccessException;
import me.kroeker.alex.anchor.jserver.model.Anchor;
import me.kroeker.alex.anchor.jserver.model.FrameInstance;
import me.kroeker.alex.anchor.jserver.model.AnchorConfigDescription;

import java.util.Collection;

public interface AnchorRule {

    Collection<Anchor> runSubmodularPick(String connectionName,
                                         String modelId,
                                         String frameId,
                                         FrameInstance instance) throws DataAccessException;

    Anchor computeRule(String connectionName,
                       String modelId,
                       String frameId,
                       FrameInstance instance) throws DataAccessException;

    Collection<AnchorConfigDescription> getAnchorConfigs(String connectionName) throws DataAccessException;
}
