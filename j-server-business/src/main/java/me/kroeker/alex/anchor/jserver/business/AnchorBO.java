package me.kroeker.alex.anchor.jserver.business;

import me.kroeker.alex.anchor.jserver.anchor.AnchorRule;
import me.kroeker.alex.anchor.jserver.api.exceptions.DataAccessException;
import me.kroeker.alex.anchor.jserver.model.Anchor;
import me.kroeker.alex.anchor.jserver.model.DataInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collection;

@Component
public class AnchorBO {

    private AnchorRule anchor;

    public AnchorBO(@Autowired AnchorRule anchor) {
        this.anchor = anchor;
    }

    public Anchor computeRule(String connectionName,
                              String modelId,
                              String frameId,
                              DataInstance instance) throws DataAccessException {
        return anchor.computeRule(connectionName, modelId, frameId, instance);
    }

    public Collection<Anchor> runSubmodularPick(String connectionName,
                                                String modelId,
                                                String frameId,
                                                DataInstance instance) throws DataAccessException {
        return anchor.runSubmodularPick(connectionName, modelId, frameId, instance);
    }

}
