package me.kroeker.alex.anchor.jserver.model;

import java.util.Objects;

/**
 *
 */
public class AnchorRuleEnum extends AnchorRule {

    private static final String COLUMN_TYPE = "string";

    private String category;

    public AnchorRuleEnum() {
        super(COLUMN_TYPE);
    }

    public AnchorRuleEnum(String featureName, String category, double precision, double coverage) {
        super(COLUMN_TYPE, featureName, precision, coverage);
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
        AnchorRuleEnum that = (AnchorRuleEnum) o;
        return Objects.equals(category, that.category);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), category);
    }

    @Override
    public String toString() {
        return "AnchorRuleEnum{" +
                "category='" + category + '\'' +
                "} " + super.toString();
    }
}
