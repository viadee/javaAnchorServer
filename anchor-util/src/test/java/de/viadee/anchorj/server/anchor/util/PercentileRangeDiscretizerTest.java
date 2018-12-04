package de.viadee.anchorj.server.anchor.util;

import org.junit.jupiter.api.Test;

import de.viadee.anchorj.server.anchor.util.PercentileRangeDiscretizer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 *
 */
class PercentileRangeDiscretizerTest {

    @Test
    void testHasFiveClassesWithDiffOfFive() {
        PercentileRangeDiscretizer discretizer = new PercentileRangeDiscretizer(5, 0, 5);
        Number[] numbers = new Number[] { 0, 1, 2, 3, 4, 5 };
        Integer[] ranges = discretizer.apply(numbers);

        assertEquals(0, ranges[0].intValue());
        assertEquals(1, ranges[1].intValue());
        assertEquals(2, ranges[2].intValue());
        assertEquals(3, ranges[3].intValue());
        assertEquals(4, ranges[4].intValue());
        assertEquals(4, ranges[5].intValue());
    }

    @Test
    void testFiveClassesWithDiffOfThree() {
        PercentileRangeDiscretizer discretizer = new PercentileRangeDiscretizer(5, 0, 3);
        Number[] numbers = new Number[] { 0, 1, 2, 3 };
        Integer[] ranges = discretizer.apply(numbers);

        assertEquals(0, ranges[0].intValue());
        assertEquals(1, ranges[1].intValue());
        assertEquals(2, ranges[2].intValue());
        assertEquals(3, ranges[3].intValue());
    }

    @Test
    void testValueIsNa() {
        PercentileRangeDiscretizer discretizer = new PercentileRangeDiscretizer(5, 0, 5);
        Number[] numbers = new Number[] { -999 };
        Integer[] ranges = discretizer.apply(numbers);

        assertEquals(-999, ranges[0].intValue());
    }

    @Test
    void testClasses() {
        PercentileRangeDiscretizer discretizer = new PercentileRangeDiscretizer(5, 5, 20);
        Number[] numbers = new Number[] { 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20 };
        Integer[] discretizedValues = discretizer.apply(numbers);

        assertEquals(0, discretizedValues[0].intValue());
        assertEquals(0, discretizedValues[1].intValue());
        assertEquals(0, discretizedValues[2].intValue());
        assertEquals(1, discretizedValues[3].intValue());
        assertEquals(1, discretizedValues[4].intValue());
        assertEquals(1, discretizedValues[5].intValue());
        assertEquals(2, discretizedValues[6].intValue());
        assertEquals(2, discretizedValues[7].intValue());
        assertEquals(2, discretizedValues[8].intValue());
        assertEquals(3, discretizedValues[9].intValue());
        assertEquals(3, discretizedValues[10].intValue());
        assertEquals(3, discretizedValues[11].intValue());
        assertEquals(4, discretizedValues[12].intValue());
        assertEquals(4, discretizedValues[13].intValue());
        assertEquals(4, discretizedValues[14].intValue());
    }

    @Test
    void testValueNotHandled() {
        assertThrows(IllegalArgumentException.class, () -> {
            PercentileRangeDiscretizer discretizer = new PercentileRangeDiscretizer(5, 0, 5);
            discretizer.apply(new Number[] { 6 });
        });
    }

}
