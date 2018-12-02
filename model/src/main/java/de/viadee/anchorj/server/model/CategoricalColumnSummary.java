package de.viadee.anchorj.server.model;

import java.util.List;
import java.util.Objects;

/**
 */
public class CategoricalColumnSummary<T extends Object> extends ColumnSummary<T> {
    private List<CategoryFreq> categories;
    private int unique;

    public List<CategoryFreq> getCategories() {
        return categories;
    }

    public void setCategories(List<CategoryFreq> categories) {
        this.categories = categories;
    }

    public int getUnique() {
        return unique;
    }

    public void setUnique(int unique) {
        this.unique = unique;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        CategoricalColumnSummary<?> that = (CategoricalColumnSummary<?>) o;
        return unique == that.unique &&
                Objects.equals(categories, that.categories);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), categories, unique);
    }

    @Override
    public String toString() {
        return "CategoricalColumnSummary{" +
                "categories=" + categories +
                ", unique=" + unique +
                "} " + super.toString();
    }
}
