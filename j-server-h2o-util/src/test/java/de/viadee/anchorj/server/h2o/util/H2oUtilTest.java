package de.viadee.anchorj.server.h2o.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 *
 */
class H2oUtilTest {

    @Test
    void testIsEnumColumn() {
        assertTrue(H2oUtil.isEnumColumn("enum"));
    }

    @Test
    void testIsStringColumn() {
        assertTrue(H2oUtil.isStringColumn("String"));
        assertTrue(H2oUtil.isStringColumn("uuid"));
    }

}
