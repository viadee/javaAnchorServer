package de.viadee.anchorj.server.dao;

import de.viadee.anchorj.server.api.exceptions.DataAccessException;
import de.viadee.anchorj.server.model.DataFrame;
import de.viadee.anchorj.server.model.FeatureConditionsRequest;
import de.viadee.anchorj.server.model.FrameInstance;
import de.viadee.anchorj.server.model.FrameSummary;

import java.util.Collection;

/**
 * Definierter Zugriff auf die Frames, die im Cluster gespeichert sind.
 */
public interface FrameDAO {

    /**
     * @param connectionName der Name des Clusters
     * @return eine Liste aller Frames, auf die im angegebenen Cluster zugegriffen werden kann
     * @throws DataAccessException wenn bei der Kommunikation mit dem Cluster Fehler aufgetreten sind
     */
    Collection<DataFrame> getFrames(String connectionName) throws DataAccessException;

    /**
     * @param connectionName der Name des Clusters
     * @param frameId        die Id des Frames
     * @return eine Zusammenfassung des Frames mit erweiterten Informationen pro Spalte
     * @throws DataAccessException wenn bei der Kommunikation mit dem Cluster Fehler aufgetreten sind
     */
    FrameSummary getFrameSummary(String connectionName, String frameId) throws DataAccessException;

    /**
     * Aus dem Frame wird ein zufälliger Datensatz gewählt. Der Raum der möglichen Datensätze kann durch conditions
     * beschränkt werden.
     *
     * @param connectionName der Name des Clusters
     * @param frameId        die Id des Frames
     * @param conditions     eine Liste von Konditionen mit dessen der Raum der Instanzen begrenzt wird
     * @return eine zufällig ausgewählte Instanz
     * @throws DataAccessException wenn bei der Kommunikation mit dem Cluster Fehler aufgetreten sind
     */
    FrameInstance randomInstance(String connectionName,
                                 String frameId,
                                 FeatureConditionsRequest conditions) throws DataAccessException;

}
