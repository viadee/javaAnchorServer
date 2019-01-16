package de.viadee.anchorj.server.model;

import java.util.Collection;
import java.util.Objects;
import java.util.Set;

/**
 *
 */
public class Model {
    private String model_id;
    private String name;
    private String url;
    private String target_column;
    private DataFrame data_frame;
    private Collection<DataFrame> compatibleFrames;
    private Set<String> ignoredColumns;

    public Model() {
    }

    public String getModel_id() {
        return model_id;
    }

    public void setModel_id(String model_id) {
        this.model_id = model_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getTarget_column() {
        return target_column;
    }

    public void setTarget_column(String target_column) {
        this.target_column = target_column;
    }

    public DataFrame getData_frame() {
        return data_frame;
    }

    public void setData_frame(DataFrame data_frame) {
        this.data_frame = data_frame;
    }

    public Collection<DataFrame> getCompatibleFrames() {
        return compatibleFrames;
    }

    public void setCompatibleFrames(Collection<DataFrame> compatibleFrames) {
        this.compatibleFrames = compatibleFrames;
    }

    public Set<String> getIgnoredColumns() {
        return ignoredColumns;
    }

    public void setIgnoredColumns(Set<String> ignoredColumns) {
        this.ignoredColumns = ignoredColumns;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Model model = (Model) o;
        return Objects.equals(model_id, model.model_id) &&
                Objects.equals(name, model.name) &&
                Objects.equals(url, model.url) &&
                Objects.equals(target_column, model.target_column) &&
                Objects.equals(data_frame, model.data_frame) &&
                Objects.equals(compatibleFrames, model.compatibleFrames) &&
                Objects.equals(ignoredColumns, model.ignoredColumns);
    }

    @Override
    public int hashCode() {
        return Objects.hash(model_id, name, url, target_column, data_frame, compatibleFrames, ignoredColumns);
    }

    @Override
    public String toString() {
        return "Model{" +
                "model_id='" + model_id + '\'' +
                ", name='" + name + '\'' +
                ", url='" + url + '\'' +
                ", target_column='" + target_column + '\'' +
                ", data_frame=" + data_frame +
                ", compatibleFrames=" + compatibleFrames +
                ", ignoredColumns=" + ignoredColumns +
                '}';
    }
}
