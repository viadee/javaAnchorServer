package me.kroeker.alex.anchor.jserver.model;

import java.io.Serializable;
import java.util.Objects;

/**
 *
 */
public class AnchorRule implements Serializable {

    private String columnType;

    private String featureName;

    private double precision;

    private double coverage;

    public AnchorRule() {
    }

    public AnchorRule(String columnType) {
        this.columnType = columnType;
    }

    public AnchorRule(String columnType, String featureName, double precision, double coverage) {
        this.columnType = columnType;
        this.featureName = featureName;
        this.precision = precision;
        this.coverage = coverage;
    }

    public String getColumnType() {
        return columnType;
    }

    public String getFeatureName() {
        return featureName;
    }

    public double getPrecision() {
        return precision;
    }

    public double getCoverage() {
        return coverage;
    }

    public void setColumnType(String columnType) {
        this.columnType = columnType;
    }

    public void setFeatureName(String featureName) {
        this.featureName = featureName;
    }

    public void setPrecision(double precision) {
        this.precision = precision;
    }

    public void setCoverage(double coverage) {
        this.coverage = coverage;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AnchorRule that = (AnchorRule) o;
        return Double.compare(that.precision, precision) == 0 &&
                Double.compare(that.coverage, coverage) == 0 &&
                Objects.equals(columnType, that.columnType) &&
                Objects.equals(featureName, that.featureName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(columnType, featureName, precision, coverage);
    }

    @Override
    public String toString() {
        return "AnchorRule{" +
                "columnType='" + columnType + '\'' +
                ", featureName='" + featureName + '\'' +
                ", precision=" + precision +
                ", coverage=" + coverage +
                '}';
    }
}
