package me.kroeker.alex.anchor.jserver.dao;

import java.util.Collection;
import java.util.Map;

import me.kroeker.alex.anchor.jserver.dao.exceptions.DataAccessException;
import me.kroeker.alex.anchor.jserver.model.FrameSummary;

/**
 * @author ak902764
 */
public interface DataDAO {

    Map<String, Collection<String>> caseSelectConditions(String h2oServer, String modelId, String frameId) throws DataAccessException;

    FrameSummary getFrame(String h2oServer, String frameId) throws DataAccessException;

}
