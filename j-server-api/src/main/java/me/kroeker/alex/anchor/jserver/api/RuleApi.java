package me.kroeker.alex.anchor.jserver.api;

import me.kroeker.alex.anchor.jserver.model.CaseSelectConditionRequest;
import me.kroeker.alex.anchor.jserver.model.Rule;

/**
 * @author ak902764
 */
public interface RuleApi {

    Rule createRule(
            String connectionName,
            String modelId,
            String frameId,
            CaseSelectConditionRequest conditions);

}
