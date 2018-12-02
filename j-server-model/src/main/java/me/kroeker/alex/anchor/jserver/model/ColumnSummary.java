package me.kroeker.alex.anchor.jserver.model;

import java.util.Collection;
import java.util.Objects;

/**
 *
 */
public abstract class ColumnSummary<T extends Object> {
    private String frame_id;
    private String label;
    private String column_type;
    private Collection<T> data;
    private long missing_count;

    public String getFrame_id() {
        return frame_id;
    }

    public void setFrame_id(String frame_id) {
        this.frame_id = frame_id;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getColumn_type() {
        return column_type;
    }

    public void setColumn_type(String column_type) {
        this.column_type = column_type;
    }

    public Collection<T> getData() {
        return data;
    }

    public void setData(Collection<T> data) {
        this.data = data;
    }

    public long getMissing_count() {
        return missing_count;
    }

    public void setMissing_count(long missing_count) {
        this.missing_count = missing_count;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ColumnSummary<?> that = (ColumnSummary<?>) o;
        return missing_count == that.missing_count &&
                Objects.equals(frame_id, that.frame_id) &&
                Objects.equals(label, that.label) &&
                Objects.equals(column_type, that.column_type) &&
                Objects.equals(data, that.data);
    }

    @Override
    public int hashCode() {
        return Objects.hash(frame_id, label, column_type, data, missing_count);
    }

    @Override
    public String toString() {
        return "ColumnSummary{" +
                "frame_id='" + frame_id + '\'' +
                ", label='" + label + '\'' +
                ", column_type='" + column_type + '\'' +
                ", data=" + data +
                ", missing_count=" + missing_count +
                '}';
    }
}
