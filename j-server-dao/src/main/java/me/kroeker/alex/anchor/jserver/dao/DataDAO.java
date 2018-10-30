package me.kroeker.alex.anchor.jserver.dao;

import de.goerke.tobias.anchorj.tabular.TabularInstance;
import me.kroeker.alex.anchor.jserver.api.exceptions.DataAccessException;
import me.kroeker.alex.anchor.jserver.model.CaseSelectCondition;
import me.kroeker.alex.anchor.jserver.model.CaseSelectConditionRequest;
import me.kroeker.alex.anchor.jserver.model.FrameSummary;

import java.util.Collection;
import java.util.Map;

/**
 * @author ak902764
 */
public interface DataDAO {

    Map<String, Collection<? extends CaseSelectCondition>> caseSelectConditions(String connectionName, String frameId) throws DataAccessException;

    FrameSummary getFrame(String connectionName, String frameId) throws DataAccessException;

    TabularInstance randomInstance(String connectionName,
                                   String modelId,
                                   String frameId,
                                   CaseSelectConditionRequest conditions) throws DataAccessException;
}
