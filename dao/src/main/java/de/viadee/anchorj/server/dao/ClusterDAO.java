package de.viadee.anchorj.server.dao;

import java.util.Collection;

/**
 * Definition einer Schnittstelle um Informationen zu verfügbaren Cluster zu erhalten als auch einen
 * Verbdindungsversuch auszuführen.
 */
public interface ClusterDAO {

    /**
     * Liefert die Namen alles verfügbaren Cluster.
     *
     * @return eine Liste alle verfügbaren Cluster.
     */
    Collection<String> getConnectionNames();

    /**
     * Versucht sich mit einem Cluster zu verbinden. Die Verbindung wird sofort wieder geschlossen.
     *
     * @param connectionName das Cluster mit welchem sich verbunden werden soll
     * @return true, wenn eine Verbindung hergestellt werden konnte andernfalls false.
     */
    boolean tryConnect(String connectionName);

}
