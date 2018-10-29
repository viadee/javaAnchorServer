package me.kroeker.alex.anchor.jserver.dao.h2o;

import java.util.HashMap;
import java.util.Map;

import water.bindings.H2oApi;

/**
 * @author ak902764
 */
public class BaseH2oAccess {

    static final Map<String, String> H2O_SERVER = new HashMap<>();

    static {
        H2O_SERVER.put("local-H2O", "http://localhost:54321");
    }

    H2oApi createH2o(String connectionName) {
        return new H2oApi(H2O_SERVER.get(connectionName));
    }

}
