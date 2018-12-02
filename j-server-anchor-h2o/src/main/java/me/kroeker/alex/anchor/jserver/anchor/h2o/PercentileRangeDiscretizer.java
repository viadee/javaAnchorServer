package me.kroeker.alex.anchor.jserver.anchor.h2o;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;

/**
 */
public class PercentileRangeDiscretizer implements Function<Number[], Integer[]>, Serializable {
    private static final long serialVersionUID = 1569809236645015291L;

    private List<Range> valueDiscretizer;

    public PercentileRangeDiscretizer(int classCount, final double min, final double max) {
        double diff = max - min;
        if (diff < classCount) {
            classCount = (int) diff;
        }
        final double step = diff / classCount;

        valueDiscretizer = new ArrayList<>(classCount);
        for (int i = 0; i < classCount; i++) {
            double stepMin = min + step * i;
            double stepMax = min + step * (i + 1);
            if (i == classCount - 1) {
                stepMax++;
            }

            this.valueDiscretizer.add(new Range(i, stepMin, stepMax));
        }
    }

    @Override
    public Integer[] apply(Number[] numbers) {
        return Stream.of(numbers).mapToInt(this::getClassOfValue).boxed().toArray(Integer[]::new);
    }

    private int getClassOfValue(Number number) {
        double value = number.doubleValue();
        for (Range range : valueDiscretizer) {
            if (range.rangeInclusiveMin <= value && range.rangeExclusiveMax > value) {
                return range.label;
            }
        }
        if (NoValueHandler.isNumberNa(number)) {
            return NoValueHandler.getNumberNa();
        }
        // TODO improve exception explanation to list all ranges
        throw new IllegalArgumentException("Number " + number + " not handled");
    }

    private static class Range implements Serializable {
        private static final long serialVersionUID = 7804017107871251851L;

        int label;
        double rangeInclusiveMin;
        double rangeExclusiveMax;

        Range(int label, double rangeInclusiveMin, double rangeExclusiveMax) {
            this.label = label;
            this.rangeInclusiveMin = rangeInclusiveMin;
            this.rangeExclusiveMax = rangeExclusiveMax;
        }
    }

}
