package me.kroeker.alex.anchor.jserver.model;

import java.util.Objects;

/**
 * @author ak902764
 */
public class CaseSelectConditionEnum extends CaseSelectCondition {
    private String category;

    public CaseSelectConditionEnum() {
    }

    public CaseSelectConditionEnum(String featureName, String category) {
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
        CaseSelectConditionEnum that = (CaseSelectConditionEnum) o;
        return Objects.equals(category, that.category);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), category);
    }

    @Override
    public String toString() {
        return "CaseSelectConditionEnum{" +
                "category='" + category + '\'' +
                "} " + super.toString();
    }

}
