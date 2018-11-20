package me.kroeker.alex.anchor.jserver.model;

import java.util.Collection;

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
}
