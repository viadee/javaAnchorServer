package de.viadee.anchorj.server.model;

import java.util.Objects;

/**
 */
public class DataFrame {
    private String frame_id;
    private String name;
    private String url;

    public String getFrame_id() {
        return frame_id;
    }

    public void setFrame_id(String frame_id) {
        this.frame_id = frame_id;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DataFrame dataFrame = (DataFrame) o;
        return Objects.equals(frame_id, dataFrame.frame_id) &&
                Objects.equals(name, dataFrame.name) &&
                Objects.equals(url, dataFrame.url);
    }

    @Override
    public int hashCode() {
        return Objects.hash(frame_id, name, url);
    }

    @Override
    public String toString() {
        return "DataFrame{" +
                "frame_id='" + frame_id + '\'' +
                ", name='" + name + '\'' +
                ", url='" + url + '\'' +
                '}';
    }
}
