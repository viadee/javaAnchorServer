package de.viadee.anchorj.server.model;

import java.util.Objects;

/**
 */
public class FeatureConditionMetric extends FeatureCondition {
    private static final long serialVersionUID = 4495509139369437182L;

    private double conditionMin;

    private double conditionMax;

    public FeatureConditionMetric() {
    }

    public FeatureConditionMetric(String featureName, double conditionMin, double conditionMax) {
        super("metric", featureName);
        this.conditionMin = conditionMin;
        this.conditionMax = conditionMax;
    }

    public double getConditionMin() {
        return conditionMin;
    }

    public void setConditionMin(double conditionMin) {
        this.conditionMin = conditionMin;
    }

    public double getConditionMax() {
        return conditionMax;
    }

    public void setConditionMax(double conditionMax) {
        this.conditionMax = conditionMax;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        FeatureConditionMetric that = (FeatureConditionMetric) o;
        return conditionMin == that.conditionMin &&
                conditionMax == that.conditionMax;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), conditionMin, conditionMax);
    }

    @Override
    public String toString() {
        return "FeatureConditionMetric{" +
                "conditionMin=" + conditionMin +
                ", conditionMax=" + conditionMax +
                "} " + super.toString();
    }

}
