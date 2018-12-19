package de.viadee.anchorj.server.model;

import java.io.Serializable;
import java.util.Objects;

/**
 *
 */
@SuppressWarnings({ "unused", "WeakerAccess" })
public class AnchorPredicate implements Serializable {
    private static final long serialVersionUID = 9101074766885725364L;

    public enum FeatureType {
        CATEGORICAL, METRIC, UNDEFINED
    }

    private static final String COLUMN_TYPE = "metric";

    private final FeatureType featureType;

    private final String featureName;

    private final Integer discretizedValue;

    private final Serializable categoricalValue;

    private final Double conditionMin;

    private final Double conditionMax;

    private final Double addedPrecision;

    private final Double addedCoverage;

    private Double exactCoverage;

    public AnchorPredicate(String featureName, Integer discretizedValue, double addedPrecision, double addedCoverage, Serializable categoricalValue) {
        this.featureType = FeatureType.CATEGORICAL;
        this.featureName = featureName;
        this.discretizedValue = discretizedValue;
        this.categoricalValue = categoricalValue;
        this.conditionMin = null;
        this.conditionMax = null;
        this.addedPrecision = addedPrecision;
        this.addedCoverage = addedCoverage;
    }

    public AnchorPredicate(String featureName, Integer discretizedValue, double addedPrecision, double addedCoverage, double conditionMin, double conditionMax) {
        this.featureType = FeatureType.METRIC;
        this.featureName = featureName;
        this.discretizedValue = discretizedValue;
        this.categoricalValue = null;
        this.conditionMin = conditionMin;
        this.conditionMax = conditionMax;
        this.addedPrecision = addedPrecision;
        this.addedCoverage = addedCoverage;
    }

    public FeatureType getFeatureType() {
        return featureType;
    }

    public String getFeatureName() {
        return featureName;
    }

    public Double getAddedPrecision() {
        return addedPrecision;
    }

    public Double getAddedCoverage() {
        return addedCoverage;
    }

    public Double getExactCoverage() {
        return exactCoverage;
    }

    public Integer getDiscretizedValue() {
        return discretizedValue;
    }

    public Serializable getCategoricalValue() {
        return categoricalValue;
    }

    public Double getConditionMin() {
        return conditionMin;
    }

    public Double getConditionMax() {
        return conditionMax;
    }

    public void setExactCoverage(Double exactCoverage) {
        this.exactCoverage = exactCoverage;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AnchorPredicate predicate = (AnchorPredicate) o;
        return featureType == predicate.featureType &&
                Objects.equals(featureName, predicate.featureName) &&
                Objects.equals(discretizedValue, predicate.discretizedValue) &&
                Objects.equals(categoricalValue, predicate.categoricalValue) &&
                Objects.equals(conditionMin, predicate.conditionMin) &&
                Objects.equals(conditionMax, predicate.conditionMax) &&
                Objects.equals(addedPrecision, predicate.addedPrecision) &&
                Objects.equals(addedCoverage, predicate.addedCoverage) &&
                Objects.equals(exactCoverage, predicate.exactCoverage);
    }

    @Override
    public int hashCode() {
        return Objects.hash(featureType, featureName, discretizedValue, categoricalValue, conditionMin, conditionMax, addedPrecision, addedCoverage, exactCoverage);
    }

    @Override
    public String toString() {
        return "AnchorPredicate{" +
                "featureType=" + featureType +
                ", featureName='" + featureName + '\'' +
                ", discretizedValue=" + discretizedValue +
                ", categoricalValue=" + categoricalValue +
                ", conditionMin=" + conditionMin +
                ", conditionMax=" + conditionMax +
                ", addedPrecision=" + addedPrecision +
                ", addedCoverage=" + addedCoverage +
                ", exactCoverage=" + exactCoverage +
                '}';
    }
}
