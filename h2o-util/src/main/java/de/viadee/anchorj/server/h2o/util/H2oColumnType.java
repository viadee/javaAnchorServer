package de.viadee.anchorj.server.h2o.util;

/**
 *
 */
public enum H2oColumnType {
    ENUM("enum"), UUID("uuid"), STRING("string"), REAL("real"), INT("int");

    private final String columnType;

    H2oColumnType(String columnType) {
        this.columnType = columnType;
    }

    public static H2oColumnType fromString(String columnType) {
        for (H2oColumnType v : H2oColumnType.values()) {
            if (v.columnType.equals(columnType)) {
                return v;
            }
        }

        return null;
    }

    public static boolean isColumnTypeReal(String columnType) {
        return REAL.columnType.equalsIgnoreCase(columnType);
    }

    public static boolean isColumnTypeInt(String columnType) {
        return INT.columnType.equalsIgnoreCase(columnType);
    }

    public static boolean isColumnTypeEnum(String columnType) {
        return ENUM.columnType.equalsIgnoreCase(columnType);
    }

    public static boolean isColumnTypeString(String columnType) {
        return STRING.columnType.equalsIgnoreCase(columnType) || UUID.columnType.equalsIgnoreCase(columnType);
    }

}
