package me.kroeker.alex.anchor.jserver.dao;

import me.kroeker.alex.anchor.jserver.dao.exceptions.DataAccessException;
import me.kroeker.alex.anchor.jserver.model.CaseSelectConditionRequest;
import me.kroeker.alex.anchor.jserver.model.Rule;

/**
 * @author ak902764
 */
public interface RuleDAO {

    Rule randomRule(
            String connectionName,
            String modelId,
            String frameId,
            CaseSelectConditionRequest conditions
    ) throws DataAccessException;

}
