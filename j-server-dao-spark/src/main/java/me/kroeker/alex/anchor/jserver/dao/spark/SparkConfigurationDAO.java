package me.kroeker.alex.anchor.jserver.dao.spark;

import me.kroeker.alex.anchor.jserver.dao.ConfigurationDAO;

/**
 * @author ak902764
 */
public class SparkConfigurationDAO implements ConfigurationDAO {
    @Override
    public boolean tryConnect(String h2oServer) {
        return false;
    }
}
