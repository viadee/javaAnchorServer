package me.kroeker.alex.anchor.jserver.business;

import de.goerke.tobias.anchorj.tabular.TabularInstance;
import me.kroeker.alex.anchor.jserver.anchor.AnchorRule;
import me.kroeker.alex.anchor.jserver.api.exceptions.DataAccessException;
import me.kroeker.alex.anchor.jserver.model.Rule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class RuleBO {

    @Autowired
    private AnchorRule anchor;

    public Rule computeRule(String connectionName,
                            String modelId,
                            String frameId,
                            TabularInstance instance) throws DataAccessException {
        return anchor.computeRule(connectionName, modelId, frameId, instance);
    }

}
