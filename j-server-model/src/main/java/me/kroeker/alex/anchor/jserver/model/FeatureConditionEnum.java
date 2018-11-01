package me.kroeker.alex.anchor.jserver.model;

import java.util.Objects;

/**
 */
public class FeatureConditionEnum extends FeatureCondition {
    private String category;

    public FeatureConditionEnum() {
    }

    public FeatureConditionEnum(String featureName, String category) {
        super("string", featureName);
        this.category = category;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        FeatureConditionEnum that = (FeatureConditionEnum) o;
        return Objects.equals(category, that.category);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), category);
    }

    @Override
    public String toString() {
        return "FeatureConditionEnum{" +
                "category='" + category + '\'' +
                "} " + super.toString();
    }

}
