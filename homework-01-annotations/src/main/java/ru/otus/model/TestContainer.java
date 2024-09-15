package ru.otus.model;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class TestContainer {

    private Method beforeSuiteMethod;
    private Method afterSuiteMethod;
    private List<Method> testMethods = new ArrayList<>();

    public Method getBeforeSuiteMethod() {
        return beforeSuiteMethod;
    }

    public void setBeforeSuiteMethod(Method beforeSuiteMethod) {
        this.beforeSuiteMethod = beforeSuiteMethod;
    }

    public Method getAfterSuiteMethod() {
        return afterSuiteMethod;
    }

    public void setAfterSuiteMethod(Method afterSuiteMethod) {
        this.afterSuiteMethod = afterSuiteMethod;
    }

    public List<Method> getTestMethods() {
        return testMethods;
    }

    public void setTestMethods(List<Method> testMethods) {
        this.testMethods = testMethods;
    }
}
