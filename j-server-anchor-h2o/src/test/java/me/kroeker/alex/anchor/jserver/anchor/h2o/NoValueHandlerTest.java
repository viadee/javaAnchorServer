package me.kroeker.alex.anchor.jserver.anchor.h2o;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 *
 */
class NoValueHandlerTest {

    @Test
    void testIsNumberNa() {
        assertTrue(NoValueHandler.isNumberNa(-999));
        assertFalse(NoValueHandler.isNumberNa(123));
        assertFalse(NoValueHandler.isNumberNa(-999.01));
        assertFalse(NoValueHandler.isNumberNa(-998.99));
    }

    @Test
    void testGetNumberNa() {
        assertEquals(-999, NoValueHandler.getNumberNa());
    }

}