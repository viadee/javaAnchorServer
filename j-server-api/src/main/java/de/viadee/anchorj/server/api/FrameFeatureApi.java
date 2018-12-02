package de.viadee.anchorj.server.api;

import de.viadee.anchorj.server.model.FeatureConditionsResponse;

/**
 */
public interface FrameFeatureApi {

    FeatureConditionsResponse getFeatureConditions(String connectionName, String frameId);

}
