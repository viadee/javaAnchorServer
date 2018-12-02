package de.viadee.anchorj.server.h2o.util;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 *
 */
public final class H2oUtil {

    private static final Map<String, String> H2O_SERVER = new HashMap<>();

    private static final String H2O_ENUM_COLUMN = "enum";
    private static final String H2O_UUID_COLUMN = "uuid";
    private static final String H2O_STRING_COLUMN = "string";

    // TODO make list of servers configurable
    static {
        H2O_SERVER.put("local-H2O", "http://localhost:54321");
    }

    private H2oUtil() {
    }

    public static Set<String> getH2oConnectionNames() {
        return H2O_SERVER.keySet();
    }

    public static String getH2oConnectionName(String connectionName) {
        return H2O_SERVER.get(connectionName);
    }

    public static boolean isEnumColumn(String columnType) {
        return H2O_ENUM_COLUMN.equalsIgnoreCase(columnType);
    }

    public static boolean isStringColumn(String columnType) {
        return H2O_STRING_COLUMN.equalsIgnoreCase(columnType) || H2O_UUID_COLUMN.equalsIgnoreCase(columnType);
    }

}
