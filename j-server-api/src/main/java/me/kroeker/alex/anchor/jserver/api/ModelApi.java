package me.kroeker.alex.anchor.jserver.api;

import java.util.Collection;

import me.kroeker.alex.anchor.jserver.model.Model;

/**
 */
public interface ModelApi {

    Collection<Model> getModels(String connectionName);

    Model getModel(String connectionName, String modelId);

}
