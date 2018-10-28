package me.kroeker.alex.anchor.jserver.dao.h2o;

import java.util.Collection;

import me.kroeker.alex.anchor.jserver.dao.RuleDAO;
import me.kroeker.alex.anchor.jserver.model.CaseSelectCondition;
import me.kroeker.alex.anchor.jserver.model.Rule;

/**
 * @author ak902764
 */
public class RuleH2o implements RuleDAO {

    @Override
    public Rule randomRule(String modelId, String frameId, Collection<CaseSelectCondition> conditions) {
        return null;
    }

}
