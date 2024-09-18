package ru.otus.model;

import java.lang.reflect.Method;

public class TestStatistic {

    private Method testMethod;
    private TestResult testResult;

    public TestStatistic(Method testMethod, TestResult testResult) {
        this.testMethod = testMethod;
        this.testResult = testResult;
    }

    public Method getTestMethod() {
        return testMethod;
    }

    public void setTestMethod(Method testMethod) {
        this.testMethod = testMethod;
    }

    public TestResult getTestResult() {
        return testResult;
    }

    public void setTestResult(TestResult testResult) {
        this.testResult = testResult;
    }
}
