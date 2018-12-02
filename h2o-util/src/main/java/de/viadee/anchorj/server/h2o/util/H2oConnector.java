package de.viadee.anchorj.server.h2o.util;

import water.bindings.H2oApi;

public interface H2oConnector {

    default H2oApi createH2o(String connectionName) {
        return new H2oApi(H2oUtil.getH2oConnectionName(connectionName));
    }

}
