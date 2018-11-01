package me.kroeker.alex.anchor.jserver.model;

import java.util.Objects;

/**
 */
public class ContinuousColumnSummary extends ColumnSummary<Double> {
    private int column_min;
    private int column_max;

    private int mean;

    public int getColumn_min() {
        return column_min;
    }

    public void setColumn_min(int column_min) {
        this.column_min = column_min;
    }

    public int getColumn_max() {
        return column_max;
    }

    public void setColumn_max(int column_max) {
        this.column_max = column_max;
    }

    public int getMean() {
        return mean;
    }

    public void setMean(int mean) {
        this.mean = mean;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        ContinuousColumnSummary that = (ContinuousColumnSummary) o;
        return column_min == that.column_min &&
                column_max == that.column_max &&
                mean == that.mean;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), column_min, column_max, mean);
    }

    @Override
    public String toString() {
        return "ContinuousColumnSummary{" +
                "column_min=" + column_min +
                ", column_max=" + column_max +
                ", mean=" + mean +
                "} " + super.toString();
    }

}
