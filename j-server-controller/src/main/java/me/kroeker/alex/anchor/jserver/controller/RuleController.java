package me.kroeker.alex.anchor.jserver.controller;

import java.util.Collection;
import java.util.Map;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import me.kroeker.alex.anchor.jserver.model.Rule;
import me.kroeker.alex.anchor.jserver.service.RuleService;

/**
 * @author ak902764
 */
@RestController
public class RuleController implements RuleService {

	@Override
	@RequestMapping(value = "/{h2oServer}/frames/{frameId}/conditions", method = RequestMethod.GET, produces = {
			"application/json" })
	public Map<String, Collection<String>> caseSelectConditions(@PathVariable String h2oServer,
			@PathVariable String modelId, @PathVariable String frameId) {
		return null;
	}

	@Override
	@RequestMapping(value = "/{h2oServer}/rule/{modelId}/{frameId}", method = RequestMethod.GET, produces = {
			"application/json" })
	public Rule createRule(@PathVariable String h2oServer, @PathVariable String modelId, @PathVariable String frameId,
			Collection<String> conditions) {
		return null;
	}

}
