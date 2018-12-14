package de.viadee.anchorj.server.model;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

/**
 */
public class CategoricalColumnSummary<T extends Serializable> extends ColumnSummary<T> {
    private List<CategoryFreq> categories;
    private int unique;

    public CategoricalColumnSummary() {
    }

    public CategoricalColumnSummary(String frame_id, String label, String column_type, Collection<T> data,
                                    long missing_count, List<CategoryFreq> categories, int unique) {
        super(frame_id, label, column_type, data, missing_count);
        this.categories = categories;
        this.unique = unique;
    }

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
