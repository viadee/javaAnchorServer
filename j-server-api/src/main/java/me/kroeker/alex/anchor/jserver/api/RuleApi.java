package me.kroeker.alex.anchor.jserver.api;

import me.kroeker.alex.anchor.jserver.model.CaseSelectConditionResponse;
import me.kroeker.alex.anchor.jserver.model.Rule;

/**
 * @author ak902764
 */
public interface RuleApi {

    Rule createRule(
            String connectionName,
            String modelId,
            String frameId,
            CaseSelectConditionResponse conditions);
}
