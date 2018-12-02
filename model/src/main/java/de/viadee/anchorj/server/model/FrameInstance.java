package de.viadee.anchorj.server.model;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Map;
import java.util.Objects;

public class FrameInstance {

    private final Serializable[] instance;

    private final Map<String, Integer> featureNamesMapping;

    public FrameInstance(Map<String, Integer> featureNamesMapping, Serializable[] instance) {
        this.instance = instance;
        this.featureNamesMapping = featureNamesMapping;
    }

    public Serializable[] getInstance() {
        return instance;
    }

    public Map<String, Integer> getFeatureNamesMapping() {
        return featureNamesMapping;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FrameInstance that = (FrameInstance) o;
        return Arrays.equals(instance, that.instance) &&
                Objects.equals(featureNamesMapping, that.featureNamesMapping);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(featureNamesMapping);
        result = 31 * result + Arrays.hashCode(instance);
        return result;
    }

    @Override
    public String toString() {
        return "FrameInstance{" +
                "instance=" + Arrays.toString(instance) +
                ", featureNames=" + featureNamesMapping +
                '}';
    }
}
