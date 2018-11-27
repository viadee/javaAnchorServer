package me.kroeker.alex.anchor.jserver.anchor.h2o;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 *
 */
public class NoValueHandlerTest {

    @Test
    public void testIsNumberNa() {
        assertTrue(NoValueHandler.isNumberNa(-999));
        assertFalse(NoValueHandler.isNumberNa(123));
        assertFalse(NoValueHandler.isNumberNa(-999.01));
        assertFalse(NoValueHandler.isNumberNa(-998.99));
    }

    @Test
    public void testGetNumberNa() {
        assertEquals(-999, NoValueHandler.getNumberNa());
    }

}
