package me.kroeker.alex.anchor.jserver.api;

import me.kroeker.alex.anchor.jserver.model.FeatureConditionsResponse;

/**
 */
public interface FrameFeatureApi {

    FeatureConditionsResponse getFeatureConditions(String connectionName, String frameId);

}
