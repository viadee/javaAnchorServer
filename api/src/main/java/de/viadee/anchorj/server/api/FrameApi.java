package de.viadee.anchorj.server.api;

import de.viadee.anchorj.server.model.DataFrame;
import de.viadee.anchorj.server.model.FrameSummary;

import java.util.Collection;

/**
 */
public interface FrameApi {

    Collection<DataFrame> getFrames(String connectionName);

    FrameSummary getFrameSummary(String connectionName, String frameId);

}
