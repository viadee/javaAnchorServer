package de.viadee.anchorj.server.h2o.util;

import de.viadee.anchorj.server.configuration.AppConfiguration;
import water.bindings.H2oApi;

public interface H2oConnector {

    default H2oApi createH2o(AppConfiguration configuration, String connectionName) {
        return new H2oApi(configuration.getH2oConnectionName(connectionName));
    }

}
