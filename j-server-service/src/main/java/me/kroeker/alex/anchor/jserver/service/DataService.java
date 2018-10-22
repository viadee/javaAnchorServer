package me.kroeker.alex.anchor.jserver.service;

import me.kroeker.alex.anchor.jserver.model.FrameSummary;

/**
 * @author ak902764
 */
public interface DataService {

    FrameSummary getFrame(String h2oServer, String frameId);

}
