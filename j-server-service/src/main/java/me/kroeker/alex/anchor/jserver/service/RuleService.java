package me.kroeker.alex.anchor.jserver.service;

import java.util.Collection;
import java.util.Map;

import me.kroeker.alex.anchor.jserver.model.Rule;

/**
 * @author ak902764
 */
public interface RuleService {
    Map<String, Collection<String>> caseSelectConditions(String h2oServer, String modelId, String frameId);

    Rule createRule(String h2oServer, String modelId, String frameId, Collection<String> conditions);
}
