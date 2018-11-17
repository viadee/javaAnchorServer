package me.kroeker.alex.anchor.jserver.anchor;

import java.util.Collection;

import de.goerke.tobias.anchorj.tabular.TabularInstance;
import me.kroeker.alex.anchor.jserver.api.exceptions.DataAccessException;
import me.kroeker.alex.anchor.jserver.model.Anchor;

public interface AnchorRule {

    Collection<Anchor> runSubmodularPick(String connectionName,
                                         String modelId,
                                         String frameId,
                                         TabularInstance instance) throws DataAccessException;

    Anchor computeRule(String connectionName,
                       String modelId,
                       String frameId,
                       TabularInstance instance) throws DataAccessException;

}
