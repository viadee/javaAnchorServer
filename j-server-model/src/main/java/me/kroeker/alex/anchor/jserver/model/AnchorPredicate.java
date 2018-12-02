package me.kroeker.alex.anchor.jserver.model;

import java.io.Serializable;
import java.util.Objects;

/**
 *
 */
public class AnchorPredicate implements Serializable {
    private static final long serialVersionUID = 7953975367579376984L;

    private String columnType;

    private String featureName;

    private double addedPrecision;

    private double addedCoverage;

    private double exactCoverage;

    public AnchorPredicate() {
    }

    public AnchorPredicate(String columnType) {
        this.columnType = columnType;
    }

    public AnchorPredicate(String columnType, String featureName, double addedPrecision, double addedCoverage) {
        this.columnType = columnType;
        this.featureName = featureName;
        this.addedPrecision = addedPrecision;
        this.addedCoverage = addedCoverage;
    }

    public String getColumnType() {
        return columnType;
    }

    public String getFeatureName() {
        return featureName;
    }

    public double getAddedPrecision() {
        return addedPrecision;
    }

    public double getAddedCoverage() {
        return addedCoverage;
    }

    public void setColumnType(String columnType) {
        this.columnType = columnType;
    }

    public void setFeatureName(String featureName) {
        this.featureName = featureName;
    }

    public void setAddedPrecision(double addedPrecision) {
        this.addedPrecision = addedPrecision;
    }

    public void setAddedCoverage(double addedCoverage) {
        this.addedCoverage = addedCoverage;
    }

    public double getExactCoverage() {
        return exactCoverage;
    }

    public void setExactCoverage(double exactCoverage) {
        this.exactCoverage = exactCoverage;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AnchorPredicate that = (AnchorPredicate) o;
        return Double.compare(that.addedPrecision, addedPrecision) == 0 &&
                Double.compare(that.addedCoverage, addedCoverage) == 0 &&
                Double.compare(that.exactCoverage, exactCoverage) == 0 &&
                Objects.equals(columnType, that.columnType) &&
                Objects.equals(featureName, that.featureName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(columnType, featureName, addedPrecision, addedCoverage, exactCoverage);
    }

    @Override
    public String toString() {
        return "AnchorPredicate{" +
                "columnType='" + columnType + '\'' +
                ", featureName='" + featureName + '\'' +
                ", addedPrecision=" + addedPrecision +
                ", addedCoverage=" + addedCoverage +
                ", exactCoverage=" + exactCoverage +
                '}';
    }
}
