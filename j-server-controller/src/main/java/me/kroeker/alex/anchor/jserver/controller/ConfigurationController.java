package me.kroeker.alex.anchor.jserver.controller;

import java.util.Collection;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import me.kroeker.alex.anchor.jserver.model.DataFrame;
import me.kroeker.alex.anchor.jserver.model.Model;
import me.kroeker.alex.anchor.jserver.service.ConfigurationService;

/**
 * @author ak902764
 */
@RestController
public class ConfigurationController implements ConfigurationService {

	@Override
	@RequestMapping(path = "/", method = RequestMethod.GET, headers = "Accept=application/json", produces = {
			"application/json" })
	public String getVersion() {
		// TODO get version implementieren
		return "hello";
	}

	@Override
	@RequestMapping(path = "/{h2oServer}/try_connect", method = RequestMethod.GET, produces = { "application/json" })
	public Boolean tryConnect(@PathVariable String h2oServer) {
		return null;
	}

	@Override
	@RequestMapping(path = "/{h2oServer}/models", method = RequestMethod.GET, produces = { "application/json" })
	public Collection<Model> getModels(@PathVariable String h2oServer) {
		return null;
	}

	@Override
	@RequestMapping(path = "/{h2oServer}/frames", method = RequestMethod.GET, produces = { "application/json" })
	public Collection<DataFrame> getFrames(@PathVariable String h2oServer) {
		return null;
	}

}
