package me.kroeker.alex.anchor.jserver.model;

import java.util.Objects;

/**
 */
public class CaseSelectCondition {

    private String columnType;

    private String featureName;

    public CaseSelectCondition() {
    }

    public CaseSelectCondition(String columnType) {
        this.columnType = columnType;
    }

    public CaseSelectCondition(String columnType, String featureName) {
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
        CaseSelectCondition that = (CaseSelectCondition) o;
        return Objects.equals(columnType, that.columnType) &&
                Objects.equals(featureName, that.featureName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(columnType, featureName);
    }

    @Override
    public String toString() {
        return "CaseSelectCondition{" +
                "columnType='" + columnType + '\'' +
                ", featureName='" + featureName + '\'' +
                '}';
    }

}
