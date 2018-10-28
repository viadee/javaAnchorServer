package me.kroeker.alex.anchor.jserver.model;

import java.util.Objects;

/**
 * @author ak902764
 */
public class CaseSelectConditionMetric extends CaseSelectCondition {

    private double conditionMin;

    private double conditionMax;

    public CaseSelectConditionMetric(String featureName, double conditionMin, double conditionMax) {
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
        CaseSelectConditionMetric that = (CaseSelectConditionMetric) o;
        return conditionMin == that.conditionMin &&
                conditionMax == that.conditionMax;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), conditionMin, conditionMax);
    }

    @Override
    public String toString() {
        return "CaseSelectConditionMetric{" +
                "conditionMin=" + conditionMin +
                ", conditionMax=" + conditionMax +
                "} " + super.toString();
    }

}
