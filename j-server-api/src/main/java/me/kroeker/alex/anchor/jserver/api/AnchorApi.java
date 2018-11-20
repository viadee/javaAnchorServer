package me.kroeker.alex.anchor.jserver.api;

import me.kroeker.alex.anchor.jserver.model.Anchor;
import me.kroeker.alex.anchor.jserver.model.AnchorConfigDescription;
import me.kroeker.alex.anchor.jserver.model.FeatureConditionsRequest;

import java.util.Collection;

/**
 * Definiert die Schnittstelle zur Berechnung von Anchor-Erklärungen.
 */
public interface AnchorApi {

    /**
     * Führt die Anchor-Analyse aus.
     *
     * @param connectionName der Name des zu verbindenden Clusters
     * @param modelId        die Id des Modells anhand dessen die Anchor-Analyse durchgeführt werden soll
     * @param frameId        die Id des Frames aus dem eine Instanz per zufall ausgewählt werden soll
     * @param conditions     Bedingungen, welches die zu erklärende Instanz erfüllen muss
     * @return den berechneten Anker
     */
    Anchor computeAnchor(
            String connectionName,
            String modelId,
            String frameId,
            FeatureConditionsRequest conditions);

    Collection<Anchor> runSubmodularPick(String connectionName,
                                         String modelId,
                                         String frameId
    );

    Collection<AnchorConfigDescription> getAnchorConfigs();

}
