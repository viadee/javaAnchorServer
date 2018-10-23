package me.kroeker.alex.anchor.jserver.service;

import java.util.Collection;
import java.util.Map;

import me.kroeker.alex.anchor.jserver.model.FrameSummary;

/**
 * @author ak902764
 */
public interface DataService {

    Map<String, Collection<String>> caseSelectConditions(String connectionName, String modelId, String frameId);

    FrameSummary getFrame(String connectionName, String frameId);

}
