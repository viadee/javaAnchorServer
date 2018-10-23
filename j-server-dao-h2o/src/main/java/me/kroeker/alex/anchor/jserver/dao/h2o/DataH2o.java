package me.kroeker.alex.anchor.jserver.dao.h2o;

import java.io.IOException;
import java.util.Collection;
import java.util.Map;

import org.springframework.stereotype.Component;
import me.kroeker.alex.anchor.jserver.dao.DataDAO;
import me.kroeker.alex.anchor.jserver.dao.exceptions.DataAccessException;
import me.kroeker.alex.anchor.jserver.model.FrameSummary;
import water.bindings.pojos.FrameKeyV3;
import water.bindings.pojos.FramesV3;

/**
 * @author ak902764
 */
@Component
public class DataH2o extends BaseH2oAccess implements DataDAO {

    @Override
    public Map<String, Collection<String>> caseSelectConditions(String h2oServer, String modelId, String frameId)
            throws DataAccessException {
        return null;
    }

    @Override
    public FrameSummary getFrame(String connectionName, String frameId) throws DataAccessException {
        FrameKeyV3 frameKey = new FrameKeyV3();
        frameKey.name = frameId;
        try {
            // TODO frame summary causes MalformedJsonException due to "NaN" value for value mean when column type
            // TODO is enum or string
            FramesV3 h2oFrame = this.createH2o(connectionName).frameSummary(frameKey);
            FrameSummary frame = new FrameSummary();

            frame.setFrame_id(h2oFrame.frameId.name);
            frame.setRow_count(h2oFrame.rowCount);

//            h2oFrame.
        } catch (IOException ioe) {
            throw new DataAccessException("Failed to retrieve frame summary of h2o with connection name: "
                    + connectionName + " and frame id: " + frameId, ioe);
        }
        return null;
    }

}
