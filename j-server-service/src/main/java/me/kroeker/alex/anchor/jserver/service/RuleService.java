package me.kroeker.alex.anchor.jserver.service;

import java.util.Collection;

import me.kroeker.alex.anchor.jserver.model.CaseSelectCondition;
import me.kroeker.alex.anchor.jserver.model.Rule;

/**
 * @author ak902764
 */
public interface RuleService {

    Rule createRule(String connectionName, String modelId, String frameId, Collection<CaseSelectCondition> conditions);
}
