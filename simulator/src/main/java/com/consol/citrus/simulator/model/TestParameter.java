package com.consol.citrus.simulator.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

/**
 * JPA entity for representing a test parameter
 */
@Entity
public class TestParameter implements Serializable {
    public enum ControlType {
        TEXTBOX,
        TEXTAREA,
        DROPDOWN
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "PARAMETER_ID")
    private Long parameterId;

    @JsonIgnore
    @ManyToOne
    private TestExecution testExecution;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private ControlType controlType;

    private String value;
    private boolean required;
    private String label;

    @Transient
    private List<TestParameterOption> options;

    public Long getParameterId() {
        return parameterId;
    }

    public void setParameterId(Long parameterId) {
        this.parameterId = parameterId;
    }

    public TestExecution getTestExecution() {
        return testExecution;
    }

    public void setTestExecution(TestExecution testExecution) {
        this.testExecution = testExecution;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public List<TestParameterOption> getOptions() {
        return options;
    }

    public void setOptions(List<TestParameterOption> options) {
        this.options = options;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ControlType getControlType() {
        return controlType;
    }

    public void setControlType(ControlType controlType) {
        this.controlType = controlType;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public boolean isRequired() {
        return required;
    }

    public void setRequired(boolean required) {
        this.required = required;
    }

    @Override
    public String toString() {
        return "TestParameter{" +
                "controlType=" + controlType +
                ", parameterId=" + parameterId +
                ", name='" + name + '\'' +
                ", value='" + value + '\'' +
                ", required=" + required +
                ", label='" + label + '\'' +
                ", options=" + options +
                '}';
    }
}
