package com.consol.citrus.simulator.model;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

/**
 * JPA entity for representing a test execution
 */
@Entity
public class TestExecution implements Serializable { // TODO MM rename to ScenarioExecution
    private static final Logger LOG = LoggerFactory.getLogger(TestExecution.class);

    public static final String EXECUTION_ID = "testExecutionId";

    public enum Status {
        ACTIVE,
        SUCCESS,
        FAILED
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "EXECUTION_ID")
    private Long executionId;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false)
    private Date startDate;

    @Temporal(TemporalType.TIMESTAMP)
    private Date endDate;

    @Column(nullable = false)
    private String testName;

    @Column(nullable = false)
    private Status status;

    @Column(length = 1000)
    private String errorMessage;

    @OneToMany(mappedBy = "testExecution", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("name ASC")
    private List<TestParameter> testParameters = new ArrayList<>();

    @OneToMany(mappedBy = "testExecution", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("actionId ASC")
    private List<TestAction> testActions = new ArrayList<>();

    public Long getExecutionId() {
        return executionId;
    }

    public void setExecutionId(Long executionId) {
        this.executionId = executionId;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public String getTestName() {
        return testName;
    }

    public void setTestName(String testName) {
        this.testName = testName;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        try {
            int size = getClass().getDeclaredField("errorMessage").getAnnotation(Column.class).length();
            int inLength = errorMessage.length();
            if (inLength > size) {
                errorMessage = errorMessage.substring(0, size);
            }
        } catch (SecurityException | NoSuchFieldException ex) {
            LOG.error(String.format("Error truncating error message", errorMessage), ex);
        }
        this.errorMessage = errorMessage;
    }

    public Collection<TestParameter> getTestParameters() {
        return testParameters;
    }

    public void addTestParameter(TestParameter testParameter) {
        testParameters.add(testParameter);
        testParameter.setTestExecution(this);
    }

    public void removeTestParameter(TestParameter testParameter) {
        testParameters.remove(testParameter);
        testParameter.setTestExecution(null);
    }

    public Collection<TestAction> getTestActions() {
        return testActions;
    }

    public void addTestAction(TestAction testAction) {
        testActions.add(testAction);
        testAction.setTestExecution(this);
    }

    public void removeTestAction(TestAction testAction) {
        testActions.remove(testAction);
        testAction.setTestExecution(null);
    }

    @Override
    public String toString() {
        return "TestExecution{" +
                "endDate=" + endDate +
                ", executionId=" + executionId +
                ", startDate=" + startDate +
                ", testName='" + testName + '\'' +
                ", status=" + status +
                ", errorMessage='" + errorMessage + '\'' +
                ", testParameters=" + testParameters +
                ", testActions=" + testActions +
                '}';
    }
}
