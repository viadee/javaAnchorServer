package me.kroeker.alex.anchor.jserver.anchor.h2o;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 *
 */
public class PercentileRangeDiscretizerTest {

    @Test
    public void testHasFiveClassesWithDiffOfFive() {
        PercentileRangeDiscretizer discretizer = new PercentileRangeDiscretizer(5, 0, 5);
        Number[] numbers = new Number[]{0, 1, 2, 3, 4, 5};
        Integer[] ranges = discretizer.apply(numbers);

        assertEquals(0, ranges[0].intValue());
        assertEquals(1, ranges[1].intValue());
        assertEquals(2, ranges[2].intValue());
        assertEquals(3, ranges[3].intValue());
        assertEquals(4, ranges[4].intValue());
    }

    @Test
    public void testFiveClassesWithDiffOfThree() {
        PercentileRangeDiscretizer discretizer = new PercentileRangeDiscretizer(5, 0, 3);
        Number[] numbers = new Number[]{0, 1, 2, 3};
        Integer[] ranges = discretizer.apply(numbers);

        assertEquals(0, ranges[0].intValue());
        assertEquals(1, ranges[1].intValue());
        assertEquals(2, ranges[2].intValue());
    }

    @Test
    public void testValueIsNa() {
        PercentileRangeDiscretizer discretizer = new PercentileRangeDiscretizer(5, 0, 5);
        Number[] numbers = new Number[]{-999};
        Integer[] ranges = discretizer.apply(numbers);

        assertEquals(-999, ranges[0].intValue());
    }

    @Test
    void testValueNotHandled() {
        PercentileRangeDiscretizer discretizer = new PercentileRangeDiscretizer(5, 0, 5);
        assertThrows(IllegalArgumentException.class, () -> {
            discretizer.apply(new Number[]{6});
        });
    }

}
