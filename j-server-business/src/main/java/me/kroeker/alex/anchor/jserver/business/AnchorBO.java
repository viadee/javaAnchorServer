package me.kroeker.alex.anchor.jserver.business;

import java.util.Collection;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import me.kroeker.alex.anchor.jserver.anchor.AnchorRule;
import me.kroeker.alex.anchor.jserver.api.exceptions.DataAccessException;
import me.kroeker.alex.anchor.jserver.model.Anchor;
import me.kroeker.alex.anchor.jserver.model.AnchorConfigDescription;
import me.kroeker.alex.anchor.jserver.model.FrameInstance;
import me.kroeker.alex.anchor.jserver.model.SubmodularPickResult;

@Component
public class AnchorBO {

    private AnchorRule anchor;

    public AnchorBO(@Autowired AnchorRule anchor) {
        this.anchor = anchor;
    }

    public Anchor computeRule(String connectionName,
                              String modelId,
                              String frameId,
                              FrameInstance instance,
                              Map<String, Object> anchorConfig) throws DataAccessException {
        return this.anchor.computeRule(connectionName, modelId, frameId, instance, anchorConfig);
    }

    public SubmodularPickResult runSubmodularPick(String connectionName,
                                                  String modelId,
                                                  String frameId,
                                                  FrameInstance instance,
                                                  Map<String, Object> anchorConfig) throws DataAccessException {
        return this.anchor.runSubmodularPick(connectionName, modelId, frameId, instance, anchorConfig);
    }

    public Collection<AnchorConfigDescription> getAnchorConfigs() throws DataAccessException {
        return this.anchor.getAnchorConfigs();
    }

}
