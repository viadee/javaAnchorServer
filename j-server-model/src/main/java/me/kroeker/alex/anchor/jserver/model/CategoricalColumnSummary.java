package me.kroeker.alex.anchor.jserver.model;

import java.util.Collection;
import java.util.Objects;

/**
 * @author ak902764
 */
public class CategoricalColumnSummary<T extends Object> extends ColumnSummary<T> {
    private Collection<CategoryFreq> categories;
    private int unique;

    public Collection<CategoryFreq> getCategories() {
        return categories;
    }

    public void setCategories(Collection<CategoryFreq> categories) {
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
