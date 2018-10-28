package me.kroeker.alex.anchor.jserver.model;

import java.util.Collection;
import java.util.Map;
import java.util.Objects;

/**
 * @author ak902764
 */
public class CaseSelectConditionResponse {
    private Map<String, Collection<CaseSelectConditionEnum>> enumConditions;

    private Map<String, Collection<CaseSelectConditionMetric>> metricConditions;

    public CaseSelectConditionResponse(
            Map<String, Collection<CaseSelectConditionEnum>> enumConditions,
            Map<String, Collection<CaseSelectConditionMetric>> metricConditions) {
        this.enumConditions = enumConditions;
        this.metricConditions = metricConditions;
    }

    public Map<String, Collection<CaseSelectConditionEnum>> getEnumConditions() {
        return enumConditions;
    }

    public void setEnumConditions(Map<String, Collection<CaseSelectConditionEnum>> enumConditions) {
        this.enumConditions = enumConditions;
    }

    public Map<String, Collection<CaseSelectConditionMetric>> getMetricConditions() {
        return metricConditions;
    }

    public void setMetricConditions(Map<String, Collection<CaseSelectConditionMetric>> metricConditions) {
        this.metricConditions = metricConditions;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CaseSelectConditionResponse that = (CaseSelectConditionResponse) o;
        return Objects.equals(enumConditions, that.enumConditions) &&
                Objects.equals(metricConditions, that.metricConditions);
    }

    @Override
    public int hashCode() {
        return Objects.hash(enumConditions, metricConditions);
    }

    @Override
    public String toString() {
        return "CaseSelectConditionResponse{" +
                "enumConditions=" + enumConditions +
                ", metricConditions=" + metricConditions +
                '}';
    }

}
