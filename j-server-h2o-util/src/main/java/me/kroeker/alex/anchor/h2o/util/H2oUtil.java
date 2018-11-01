package me.kroeker.alex.anchor.h2o.util;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import water.bindings.H2oApi;

/**
 */
public final class H2oUtil {

    static final Map<String, String> H2O_SERVER = new HashMap<>();

    // TODO make list of servers configurable
    static {
        H2O_SERVER.put("local-H2O", "http://localhost:54321");
    }

    private H2oUtil() {
    }

    public static Collection<String> getH2oConnectionNames() {
        return H2O_SERVER.keySet();
    }

    public static H2oApi createH2o(String connectionName) {
        return new H2oApi(H2O_SERVER.get(connectionName));
    }

    public static boolean isEnumColumn(String columnType) {
        return "enum".equals(columnType);
    }

    public static boolean isStringColumn(String columnType) {
        return "string".equals(columnType) || "uuid".equals(columnType);
    }

}
