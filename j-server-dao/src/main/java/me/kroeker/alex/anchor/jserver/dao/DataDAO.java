package me.kroeker.alex.anchor.jserver.dao;

import me.kroeker.alex.anchor.jserver.dao.exceptions.DataAccessException;
import me.kroeker.alex.anchor.jserver.model.CaseSelectCondition;
import me.kroeker.alex.anchor.jserver.model.FrameSummary;

import java.util.Collection;
import java.util.Map;

/**
 * @author ak902764
 */
public interface DataDAO {

    Map<String, Collection<? extends CaseSelectCondition>> caseSelectConditions(String connectionName, String frameId) throws DataAccessException;

    FrameSummary getFrame(String connectionName, String frameId) throws DataAccessException;

}
