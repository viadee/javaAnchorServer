package me.kroeker.alex.anchor.jserver.api;

import me.kroeker.alex.anchor.jserver.model.CaseSelectConditionResponse;

/**
 * @author ak902764
 */
public interface FrameColumnApi {

    CaseSelectConditionResponse getCaseSelectConditions(String connectionName, String frameId);

}
