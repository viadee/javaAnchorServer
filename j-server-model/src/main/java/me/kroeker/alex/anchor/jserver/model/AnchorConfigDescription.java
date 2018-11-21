package me.kroeker.alex.anchor.jserver.model;

import java.util.Objects;

public class AnchorConfigDescription {
    private final String configName;
    private final ConfigInputType inputType;
    private final Object defaultValue;

    public AnchorConfigDescription(String configName, ConfigInputType inputType, Object defaultValue) {
        this.configName = configName;
        this.inputType = inputType;
        this.defaultValue = defaultValue;
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

    public Object getDefaultValue() {
        return defaultValue;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AnchorConfigDescription that = (AnchorConfigDescription) o;
        return Objects.equals(configName, that.configName) &&
                inputType == that.inputType &&
                Objects.equals(defaultValue, that.defaultValue);
    }

    @Override
    public int hashCode() {
        return Objects.hash(configName, inputType, defaultValue);
    }

    @Override
    public String toString() {
        return "AnchorConfigDescription{" +
                "configName='" + configName + '\'' +
                ", inputType=" + inputType +
                ", defaultValue=" + defaultValue +
                '}';
    }
}
