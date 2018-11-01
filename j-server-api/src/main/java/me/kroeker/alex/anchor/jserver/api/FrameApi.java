package me.kroeker.alex.anchor.jserver.api;

import java.util.Collection;

import me.kroeker.alex.anchor.jserver.model.DataFrame;
import me.kroeker.alex.anchor.jserver.model.FrameSummary;

/**
 */
public interface FrameApi {

    Collection<DataFrame> getFrames(String connectionName);

    FrameSummary getFrameSummary(String connectionName, String frameId);

}
