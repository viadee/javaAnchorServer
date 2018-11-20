package me.kroeker.alex.anchor.jserver.api;

import me.kroeker.alex.anchor.jserver.model.Model;

import java.util.Collection;

/**
 */
public interface ModelApi {

    Collection<Model> getModels(String connectionName);

    Model getModel(String connectionName, String modelId);

}
