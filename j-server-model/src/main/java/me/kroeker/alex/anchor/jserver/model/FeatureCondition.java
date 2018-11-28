package me.kroeker.alex.anchor.jserver.model;

import java.io.Serializable;
import java.util.Objects;

/**
 */
public class FeatureCondition implements Serializable {

    private String columnType;

    private String featureName;

    public FeatureCondition() {
    }

    public FeatureCondition(String columnType) {
        this.columnType = columnType;
    }

    public FeatureCondition(String columnType, String featureName) {
        this.columnType = columnType;
        this.featureName = featureName;
    }

    public String getColumnType() {
        return columnType;
    }

    public void setColumnType(String columnType) {
        this.columnType = columnType;
    }

    public String getFeatureName() {
        return featureName;
    }

    public void setFeatureName(String featureName) {
        this.featureName = featureName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FeatureCondition that = (FeatureCondition) o;
        return Objects.equals(columnType, that.columnType) &&
                Objects.equals(featureName, that.featureName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(columnType, featureName);
    }

    @Override
    public String toString() {
        return "FeatureCondition{" +
                "columnType='" + columnType + '\'' +
                ", featureName='" + featureName + '\'' +
                '}';
    }

}
