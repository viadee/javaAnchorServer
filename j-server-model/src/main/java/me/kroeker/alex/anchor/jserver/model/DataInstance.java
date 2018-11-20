package me.kroeker.alex.anchor.jserver.model;

import java.util.Arrays;
import java.util.Map;
import java.util.Objects;

public class DataInstance {

    private final Object[] instance;

    private final Map<String, Integer> featureNamesMapping;

    public DataInstance(Map<String, Integer> featureNamesMapping, Object[] instance) {
        this.instance = instance;
        this.featureNamesMapping = featureNamesMapping;
    }

    public Object[] getInstance() {
        return instance;
    }

    public Map<String, Integer> getFeatureNamesMapping() {
        return featureNamesMapping;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DataInstance that = (DataInstance) o;
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
        return "DataInstance{" +
                "instance=" + Arrays.toString(instance) +
                ", featureNames=" + featureNamesMapping +
                '}';
    }
}
