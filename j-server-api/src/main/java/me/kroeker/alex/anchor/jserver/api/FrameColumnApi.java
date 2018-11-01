package me.kroeker.alex.anchor.jserver.api;

import me.kroeker.alex.anchor.jserver.model.CaseSelectConditionResponse;

/**
 */
public interface FrameColumnApi {

    CaseSelectConditionResponse getCaseSelectConditions(String connectionName, String frameId);

}
