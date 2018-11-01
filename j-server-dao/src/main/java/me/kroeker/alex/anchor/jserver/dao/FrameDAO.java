package me.kroeker.alex.anchor.jserver.dao;

import java.util.Collection;

import de.goerke.tobias.anchorj.tabular.TabularInstance;
import me.kroeker.alex.anchor.jserver.api.exceptions.DataAccessException;
import me.kroeker.alex.anchor.jserver.model.CaseSelectConditionRequest;
import me.kroeker.alex.anchor.jserver.model.DataFrame;
import me.kroeker.alex.anchor.jserver.model.FrameSummary;

/**
 */
public interface FrameDAO {

    Collection<DataFrame> getFrames(String connectionName) throws DataAccessException;

    FrameSummary getFrameSummary(String connectionName, String frameId) throws DataAccessException;

    TabularInstance randomInstance(String connectionName,
                                   String frameId,
                                   CaseSelectConditionRequest conditions) throws DataAccessException;

}
