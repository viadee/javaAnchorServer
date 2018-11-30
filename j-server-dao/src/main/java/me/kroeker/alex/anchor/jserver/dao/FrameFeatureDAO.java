package me.kroeker.alex.anchor.jserver.dao;

import me.kroeker.alex.anchor.jserver.api.exceptions.DataAccessException;
import me.kroeker.alex.anchor.jserver.model.FeatureCondition;

import java.util.Collection;
import java.util.Map;

/**
 * Eine Schnittstelle zum Zugriff auf die Features eines Frames.
 */
public interface FrameFeatureDAO {

    /**
     * @param connectionName der Name des Clusters
     * @param frameId        die Id des Frames
     * @return eine Karte mit allen Features. Darin enthalten ist jeweils eine Liste mit den möglichen Konditionen
     * @throws DataAccessException wenn bei der Kommunikation mit dem Cluster Fehler aufgetreten sind
     */
    Map<String, Collection<? extends FeatureCondition>> getFeatureConditions(
            String connectionName,
            String frameId)
            throws DataAccessException;

}
