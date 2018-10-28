package me.kroeker.alex.anchor.jserver.service;

import me.kroeker.alex.anchor.jserver.model.CaseSelectConditionResponse;
import me.kroeker.alex.anchor.jserver.model.FrameSummary;

/**
 * @author ak902764
 */
public interface DataService {

    CaseSelectConditionResponse caseSelectConditions(String connectionName, String frameId);

    FrameSummary getFrame(String connectionName, String frameId);

}
