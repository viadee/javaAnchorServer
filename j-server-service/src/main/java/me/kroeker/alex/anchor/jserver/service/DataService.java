package me.kroeker.alex.anchor.jserver.service;

import me.kroeker.alex.anchor.jserver.model.FrameSummary;

import java.util.Map;

/**
 * @author ak902764
 */
public interface DataService {

    Map<String, Map<Integer, String>> caseSelectConditions(String connectionName, String frameId);

    FrameSummary getFrame(String connectionName, String frameId);

}
