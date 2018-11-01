package me.kroeker.alex.anchor.jserver.model;

import java.util.Map;
import java.util.Objects;

/**
 */
public class CaseSelectConditionRequest {
    private Map<String, CaseSelectConditionEnum> enumConditions;
    private Map<String, CaseSelectConditionMetric> metricConditions;

    public CaseSelectConditionRequest() {
    }

    public Map<String, CaseSelectConditionEnum> getEnumConditions() {
        return enumConditions;
    }

    public void setEnumConditions(Map<String, CaseSelectConditionEnum> enumConditions) {
        this.enumConditions = enumConditions;
    }

    public Map<String, CaseSelectConditionMetric> getMetricConditions() {
        return metricConditions;
    }

    public void setMetricConditions(Map<String, CaseSelectConditionMetric> metricConditions) {
        this.metricConditions = metricConditions;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CaseSelectConditionRequest that = (CaseSelectConditionRequest) o;
        return Objects.equals(enumConditions, that.enumConditions) &&
                Objects.equals(metricConditions, that.metricConditions);
    }

    @Override
    public int hashCode() {
        return Objects.hash(enumConditions, metricConditions);
    }

    @Override
    public String toString() {
        return "CaseSelectConditionRequest{" +
                "enumConditions=" + enumConditions +
                ", metricConditions=" + metricConditions +
                '}';
    }

}
