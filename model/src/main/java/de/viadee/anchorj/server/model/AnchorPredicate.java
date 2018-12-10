package de.viadee.anchorj.server.model;

import java.io.Serializable;
import java.util.Objects;

/**
 *
 */
public class AnchorPredicate implements Serializable {
    private static final long serialVersionUID = -900326553380583758L;

    private String columnType;

    private String featureName;

    private Integer discretizedValue;

    private double addedPrecision;

    private double addedCoverage;

    private double exactCoverage;

    public AnchorPredicate() {
    }

    public AnchorPredicate(String columnType) {
        this.columnType = columnType;
    }

    public AnchorPredicate(String columnType, String featureName, Integer discretizedValue, double addedPrecision, double addedCoverage) {
        this.columnType = columnType;
        this.featureName = featureName;
        this.discretizedValue = discretizedValue;
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

    public Integer getDiscretizedValue() {
        return discretizedValue;
    }

    public void setDiscretizedValue(Integer discretizedValue) {
        this.discretizedValue = discretizedValue;
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
                Objects.equals(featureName, that.featureName) &&
                Objects.equals(discretizedValue, that.discretizedValue);
    }

    @Override
    public int hashCode() {
        return Objects.hash(columnType, featureName, discretizedValue, addedPrecision, addedCoverage, exactCoverage);
    }

    @Override
    public String toString() {
        return "AnchorPredicate{" +
                "columnType='" + columnType + '\'' +
                ", featureName='" + featureName + '\'' +
                ", discretizedValue=" + discretizedValue +
                ", addedPrecision=" + addedPrecision +
                ", addedCoverage=" + addedCoverage +
                ", exactCoverage=" + exactCoverage +
                '}';
    }
}
