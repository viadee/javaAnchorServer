package me.kroeker.alex.anchor.jserver.controller;

import me.kroeker.alex.anchor.jserver.dao.DataDAO;
import me.kroeker.alex.anchor.jserver.dao.exceptions.DataAccessException;
import me.kroeker.alex.anchor.jserver.model.FrameSummary;
import me.kroeker.alex.anchor.jserver.service.DataService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;
import java.util.Map;

/**
 * @author ak902764
 */
@RestController
public class DataController implements DataService {

    private static final Logger LOG = LoggerFactory.getLogger(DataController.class);

    @Autowired
    private DataDAO dataDAO;

    @Override
    @RequestMapping(value = "/{connectionName}/frames/{frameId}", method = RequestMethod.GET, produces = {
            "application/json" })
    public FrameSummary getFrame(@PathVariable String connectionName, @PathVariable String frameId) {
        try {
            return this.dataDAO.getFrame(connectionName, frameId);
        } catch (DataAccessException dae) {
            LOG.error(dae.getMessage(), dae);
            // TODO add exception handling
            return null;
        }
    }

    @Override
    @RequestMapping(value = "/{connectionName}/frames/{frameId}/conditions", method = RequestMethod.GET, produces = {
            "application/json" })
    public Map<String, Collection<String>> caseSelectConditions(@PathVariable String connectionName,
                                                                @PathVariable String modelId,
                                                                @PathVariable String frameId) {
        try {
            return this.dataDAO.caseSelectConditions(connectionName, modelId, frameId);
        } catch (DataAccessException dae) {
            LOG.error(dae.getMessage(), dae);
            // TODO add exception handling
            return null;
        }
    }

}
