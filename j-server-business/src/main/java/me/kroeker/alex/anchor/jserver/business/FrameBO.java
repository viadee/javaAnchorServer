package me.kroeker.alex.anchor.jserver.business;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import de.goerke.tobias.anchorj.tabular.TabularInstance;
import me.kroeker.alex.anchor.jserver.api.exceptions.DataAccessException;
import me.kroeker.alex.anchor.jserver.dao.FrameDAO;
import me.kroeker.alex.anchor.jserver.model.DataFrame;
import me.kroeker.alex.anchor.jserver.model.FeatureConditionsRequest;
import me.kroeker.alex.anchor.jserver.model.FrameSummary;

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

    public TabularInstance randomInstance(String connectionName,
                                          String frameId) throws DataAccessException {
        return this.randomInstance(connectionName, frameId, null);
    }

    public TabularInstance randomInstance(String connectionName,
                                          String frameId,
                                          FeatureConditionsRequest conditions) throws DataAccessException {
        return this.frameDAO.randomInstance(connectionName, frameId, conditions);
    }

}
