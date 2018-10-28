package me.kroeker.alex.anchor.jserver.dao;

import java.util.Collection;

import me.kroeker.alex.anchor.jserver.model.CaseSelectCondition;
import me.kroeker.alex.anchor.jserver.model.Rule;

/**
 * @author ak902764
 */
public interface RuleDAO {

    Rule randomRule(String modelId, String frameId, Collection<? extends CaseSelectCondition> conditions);

}
