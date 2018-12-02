package de.viadee.anchorj.server.business;

import de.viadee.anchorj.server.api.exceptions.DataAccessException;
import de.viadee.anchorj.server.dao.FrameDAO;
import de.viadee.anchorj.server.model.DataFrame;
import de.viadee.anchorj.server.model.FeatureConditionsRequest;
import de.viadee.anchorj.server.model.FrameInstance;
import de.viadee.anchorj.server.model.FrameSummary;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collection;

/**
 */
@Component
public class FrameBO {

    private FrameDAO frameDAO;

    public FrameBO(@Autowired FrameDAO frameDAO) {
        this.frameDAO = frameDAO;
    }

    public Collection<DataFrame> getFrames(String connectionName) throws DataAccessException {
        return this.frameDAO.getFrames(connectionName);
    }

    public FrameSummary getFrameSummary(String connectionName, String frameId) throws DataAccessException {
        return this.frameDAO.getFrameSummary(connectionName, frameId);
    }

    public FrameInstance randomInstance(String connectionName,
                                        String frameId) throws DataAccessException {
        return this.randomInstance(connectionName, frameId, null);
    }

    public FrameInstance randomInstance(String connectionName,
                                        String frameId,
                                        FeatureConditionsRequest conditions) throws DataAccessException {
        return this.frameDAO.randomInstance(connectionName, frameId, conditions);
    }

}
