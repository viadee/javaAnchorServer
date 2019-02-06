package de.viadee.anchorj.server.configuration;

import java.util.Set;

public interface AppConfiguration {

    Set<String> getConnectionNames();

    String getConnectionName(String connectionName);

    String getSparkLibFolder();

    String getSparkMasterUrl();

}
