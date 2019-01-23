package de.viadee.anchorj.server.h2o.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 *
 */
class H2OColumnTypeTest {

    @Test
    void testIsEnumColumn() {
        assertTrue(H2oColumnType.isColumnTypeEnum("enum"));
    }

    @Test
    void testIsStringColumn() {
        assertTrue(H2oColumnType.isColumnTypeString("String"));
        assertTrue(H2oColumnType.isColumnTypeString("uuid"));
    }

}
