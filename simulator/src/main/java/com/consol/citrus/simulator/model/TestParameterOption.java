package com.consol.citrus.simulator.model;

import java.io.Serializable;

/**
 * Used for drop-down lists when displaying a list of options
 */
public class TestParameterOption implements Serializable { // TODO MM rename to ScenarioParameterOption
    private String key;
    private String value;

    public TestParameterOption() {
    }

    public TestParameterOption(String key, String value) {
        this.key = key;
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "TestParameterOption{" +
                "key='" + key + '\'' +
                ", value='" + value + '\'' +
                '}';
    }
}
