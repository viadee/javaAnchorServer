package de.viadee.anchorj.server.h2o.util;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 *
 */
public final class H2oUtil {

    private static final Map<String, String> H2O_SERVER = new HashMap<>();

    private static final String H2O_COLUMN_TYPE_ENUM = "enum";
    private static final String H2O_COLUMN_TYPE_UUID = "uuid";
    private static final String H2O_COLUMN_TYPE_STRING = "string";
    private static final String H2O_COLUMN_TYPE_REAL = "real";
    private static final String H2O_COLUMN_TYPE_INT = "int";

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

    public static boolean isColumnTypeReal(String columnType) {
        return H2O_COLUMN_TYPE_REAL.equalsIgnoreCase(columnType);
    }


    public static boolean isColumnTypeInt(String columnType) {
        return H2O_COLUMN_TYPE_INT.equalsIgnoreCase(columnType);
    }


    public static boolean isColumnTypeEnum(String columnType) {
        return H2O_COLUMN_TYPE_ENUM.equalsIgnoreCase(columnType);
    }

    public static boolean isColumnTypeString(String columnType) {
        return H2O_COLUMN_TYPE_STRING.equalsIgnoreCase(columnType) || H2O_COLUMN_TYPE_UUID.equalsIgnoreCase(columnType);
    }

}
