package me.kroeker.alex.anchor.jserver.api;

import me.kroeker.alex.anchor.jserver.model.DataFrame;
import me.kroeker.alex.anchor.jserver.model.FrameSummary;

import java.util.Collection;

/**
 */
public interface FrameApi {

    Collection<DataFrame> getFrames(String connectionName);

    FrameSummary getFrameSummary(String connectionName, String frameId);

}
