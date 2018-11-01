package me.kroeker.alex.anchor.jserver.anchor;

import de.goerke.tobias.anchorj.tabular.TabularInstance;
import me.kroeker.alex.anchor.jserver.api.exceptions.DataAccessException;
import me.kroeker.alex.anchor.jserver.model.Anchor;

public interface AnchorRule {

    Anchor computeRule(String connectionName,
                       String modelId,
                       String frameId,
                       TabularInstance instance) throws DataAccessException;

}
