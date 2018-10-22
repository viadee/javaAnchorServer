package me.kroeker.alex.anchor.jserver.controller;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import me.kroeker.alex.anchor.jserver.model.FrameSummary;
import me.kroeker.alex.anchor.jserver.service.DataService;

/**
 * @author ak902764
 */
@RestController
public class DataController implements DataService {

	@Override
	@RequestMapping(value = "/{h2oServer}/frames/{frameId}", method = RequestMethod.GET, produces = {
			"application/json" })
	public FrameSummary getFrame(@PathVariable String h2oServer, @PathVariable String frameId) {
		return null;
	}

}
