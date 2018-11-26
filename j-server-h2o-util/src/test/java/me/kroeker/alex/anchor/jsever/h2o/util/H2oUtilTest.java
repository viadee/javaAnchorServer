package me.kroeker.alex.anchor.jsever.h2o.util;

import org.junit.jupiter.api.Test;
import me.kroeker.alex.anchor.jserver.h2o.util.H2oUtil;

import static org.junit.jupiter.api.Assertions.*;

/**
 */
public class H2oUtilTest {

    @Test
    public void testIsEnumColumn() {
        assertTrue(H2oUtil.isEnumColumn("enum"));
    }

    @Test
    public void testIsStringColumn() {
        assertTrue(H2oUtil.isStringColumn("String"));
        assertTrue(H2oUtil.isStringColumn("uuid"));
    }

}
