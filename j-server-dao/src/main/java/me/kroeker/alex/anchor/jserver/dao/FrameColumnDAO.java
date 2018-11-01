package me.kroeker.alex.anchor.jserver.dao;

import java.util.Collection;
import java.util.Map;

import me.kroeker.alex.anchor.jserver.api.exceptions.DataAccessException;
import me.kroeker.alex.anchor.jserver.model.CaseSelectCondition;

/**
 * @author ak902764
 */
public interface FrameColumnDAO {

    Map<String, Collection<? extends CaseSelectCondition>> caseSelectConditions(
            String connectionName,
            String frameId)
            throws DataAccessException;

}
