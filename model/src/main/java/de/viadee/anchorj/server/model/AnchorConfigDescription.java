package de.viadee.anchorj.server.model;

import java.util.Objects;

public class AnchorConfigDescription {
    private final String configName;
    private final ConfigInputType inputType;
    private final Object value;

    public AnchorConfigDescription(String configName, ConfigInputType inputType, Object value) {
        this.configName = configName;
        this.inputType = inputType;
        this.value = value;
    }

    public enum ConfigInputType {
        INTEGER, DOUBLE, STRING
    }

    public String getConfigName() {
        return configName;
    }

    public ConfigInputType getInputType() {
        return inputType;
    }

    public Object getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AnchorConfigDescription that = (AnchorConfigDescription) o;
        return Objects.equals(configName, that.configName) &&
                inputType == that.inputType &&
                Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(configName, inputType, value);
    }

    @Override
    public String toString() {
        return "AnchorConfigDescription{" +
                "configName='" + configName + '\'' +
                ", inputType=" + inputType +
                ", value=" + value +
                '}';
    }
}
