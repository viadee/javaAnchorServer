package de.viadee.anchorj.server.business;

import java.util.Collection;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import de.viadee.anchorj.server.anchor.AnchorRule;
import de.viadee.anchorj.server.api.exceptions.DataAccessException;
import de.viadee.anchorj.server.model.Anchor;
import de.viadee.anchorj.server.model.AnchorConfigDescription;
import de.viadee.anchorj.server.model.FrameInstance;
import de.viadee.anchorj.server.model.SubmodularPickResult;

@Component
public class AnchorBO {

    private AnchorRule localAnchor;
    private AnchorRule sparkAnchor;

    public AnchorBO(@Autowired @Qualifier("local") AnchorRule localAnchor,
                    @Autowired @Qualifier("spark") AnchorRule sparkAnchor) {
        this.localAnchor = localAnchor;
        this.sparkAnchor = sparkAnchor;
    }

    public Anchor computeRule(String connectionName,
                              String modelId,
                              String frameId,
                              FrameInstance instance,
                              Map<String, Object> anchorConfig) throws DataAccessException {
        return this.localAnchor.computeRule(connectionName, modelId, frameId, instance, anchorConfig, null);
    }

    public SubmodularPickResult runSubmodularPick(String connectionName,
                                                  String modelId,
                                                  String frameId,
                                                  FrameInstance instance,
                                                  Map<String, Object> anchorConfig) throws DataAccessException {
        return this.sparkAnchor.runSubmodularPick(connectionName, modelId, frameId, instance, anchorConfig);
    }

    public Collection<AnchorConfigDescription> getAnchorConfigs() throws DataAccessException {
        return this.localAnchor.getAnchorConfigs();
    }

}
