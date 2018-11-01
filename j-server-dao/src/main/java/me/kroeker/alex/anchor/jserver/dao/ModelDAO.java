package me.kroeker.alex.anchor.jserver.dao;

import java.util.Collection;

import me.kroeker.alex.anchor.jserver.api.exceptions.DataAccessException;
import me.kroeker.alex.anchor.jserver.model.Model;

/**
 * Definierter Zugriff auf die Modelle, die im Cluster gespeichert sind.
 */
public interface ModelDAO {

    /**
     * Anhand von connectionName und der modelId wird die Definition des Modells aus dem Cluster geladen und in
     * {@link Model} transferriert.
     *
     * @param connectionName der Name des Clusters, in dessen das Modell zu suchen ist
     * @param modelId        die Id des Modells
     * @return die aus dem Cluster und der modelId transferrierte Modell.
     * @throws DataAccessException wenn bei der Kommunikation mit dem Cluster Fehler aufgetreten sind
     */
    Model getModel(String connectionName, String modelId) throws DataAccessException;

    /**
     * Liefert alle Modelle aus dem Cluster mit dem Namen aus connectionName.
     * @param connectionName der Name des ausgewählten Clusters
     * @return eine Liste aller Modelle auf die im Cluster zugegriffen werden kann
     * @throws DataAccessException wenn bei der Kommunikation mit dem Cluster Fehler aufgetreten sind
     */
    Collection<Model> getModels(String connectionName) throws DataAccessException;

}
