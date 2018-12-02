package de.viadee.anchorj.server.api;

import de.viadee.anchorj.server.model.Model;

import java.util.Collection;

/**
 */
public interface ModelApi {

    Collection<Model> getModels(String connectionName);

    Model getModel(String connectionName, String modelId);

}
