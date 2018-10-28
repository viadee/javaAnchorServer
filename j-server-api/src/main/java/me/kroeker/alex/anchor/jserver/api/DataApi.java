package me.kroeker.alex.anchor.jserver.api;

import me.kroeker.alex.anchor.jserver.model.CaseSelectConditionResponse;
import me.kroeker.alex.anchor.jserver.model.FrameSummary;

/**
 * @author ak902764
 */
public interface DataApi {

    CaseSelectConditionResponse caseSelectConditions(String connectionName, String frameId);

    FrameSummary getFrame(String connectionName, String frameId);

}
