package me.kroeker.alex.anchor.jserver.api;

import java.util.Collection;

import me.kroeker.alex.anchor.jserver.model.Model;

/**
 * @author ak902764
 */
public interface ModelApi {

    Collection<Model> getModels(String connectionName);

    Model getModel(String connectionName, String modelId);

}
