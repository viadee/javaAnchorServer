package me.kroeker.alex.anchor.jserver.service;

import java.util.Collection;

import me.kroeker.alex.anchor.jserver.model.DataFrame;
import me.kroeker.alex.anchor.jserver.model.Model;

/**
 * @author ak902764
 */
public interface ConfigurationService {

    String getVersion();

    Boolean tryConnect(String h2oModel);

    Collection<Model> getModels(String h2oModel);

    Collection<DataFrame> getFrames(String h2oModel);

}
