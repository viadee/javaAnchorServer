package me.kroeker.alex.anchor.jserver.model;

import java.util.Collection;
import java.util.Map;
import java.util.Objects;

/**
 */
public class FeatureConditionsResponse {
    private Map<String, Collection<FeatureConditionEnum>> enumConditions;

    private Map<String, Collection<FeatureConditionMetric>> metricConditions;

    public FeatureConditionsResponse() {
    }

    public FeatureConditionsResponse(
            Map<String, Collection<FeatureConditionEnum>> enumConditions,
            Map<String, Collection<FeatureConditionMetric>> metricConditions) {
        this.enumConditions = enumConditions;
        this.metricConditions = metricConditions;
    }

    public Map<String, Collection<FeatureConditionEnum>> getEnumConditions() {
        return enumConditions;
    }

    public void setEnumConditions(Map<String, Collection<FeatureConditionEnum>> enumConditions) {
        this.enumConditions = enumConditions;
    }

    public Map<String, Collection<FeatureConditionMetric>> getMetricConditions() {
        return metricConditions;
    }

    public void setMetricConditions(Map<String, Collection<FeatureConditionMetric>> metricConditions) {
        this.metricConditions = metricConditions;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FeatureConditionsResponse that = (FeatureConditionsResponse) o;
        return Objects.equals(enumConditions, that.enumConditions) &&
                Objects.equals(metricConditions, that.metricConditions);
    }

    @Override
    public int hashCode() {
        return Objects.hash(enumConditions, metricConditions);
    }

    @Override
    public String toString() {
        return "FeatureConditionsResponse{" +
                "enumConditions=" + enumConditions +
                ", metricConditions=" + metricConditions +
                '}';
    }

}
