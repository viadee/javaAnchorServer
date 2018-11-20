package me.kroeker.alex.anchor.jserver.model;

import java.util.Collection;
import java.util.Objects;

public class AnchorConfigDescription {
    private final String configName;
    private final ConfigInputType inputType;
    private final Collection<Object> defaultValues;

    public AnchorConfigDescription(String configName, ConfigInputType inputType, Collection<Object> defaultValues) {
        this.configName = configName;
        this.inputType = inputType;
        this.defaultValues = defaultValues;
    }

    public enum ConfigInputType {
        INTEGER("int"), DOUBLE("double"), STRING("string"), ARRAY("array");

        private final String name;

        ConfigInputType(String name) {
            this.name = name;
        }
    }

    public String getConfigName() {
        return configName;
    }

    public ConfigInputType getInputType() {
        return inputType;
    }

    public Collection<Object> getDefaultValues() {
        return defaultValues;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AnchorConfigDescription that = (AnchorConfigDescription) o;
        return Objects.equals(configName, that.configName) &&
                inputType == that.inputType &&
                Objects.equals(defaultValues, that.defaultValues);
    }

    @Override
    public int hashCode() {
        return Objects.hash(configName, inputType, defaultValues);
    }

    @Override
    public String toString() {
        return "AnchorConfigDescription{" +
                "configName='" + configName + '\'' +
                ", inputType=" + inputType +
                ", defaultValues=" + defaultValues +
                '}';
    }
}
