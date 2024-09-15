package ru.otus.handler;

import ru.otus.annotation.AfterSuite;
import ru.otus.annotation.BeforeSuite;
import ru.otus.annotation.Disabled;
import ru.otus.annotation.Test;
import ru.otus.exception.InvalidOperationException;
import ru.otus.exception.InvalidTestConfigurationException;
import ru.otus.model.TestContainer;
import ru.otus.model.TestResult;
import ru.otus.model.TestStatistic;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class TestRunner {

    public static void run(Class<?> testSuiteClass) {
        if (testSuiteClass.isAnnotationPresent(Disabled.class)) {
            processDisabledAnnotation(testSuiteClass);
            return;
        }

        TestContainer container = new TestContainer();
        for (Method method : testSuiteClass.getDeclaredMethods()) {
            validateAndPrepareContainerAttributes(method, container);
        }

        List<TestStatistic> statistics = invoke(container);
        TestStatisticHelper.process(statistics, testSuiteClass);
    }

    private static void processDisabledAnnotation(Class<?> testSuiteClass) {
        Disabled disabledAnnotation = testSuiteClass.getDeclaredAnnotation(Disabled.class);
        String message = "Все тесты отключены";

        if (!disabledAnnotation.cause().isBlank()) {
            message = disabledAnnotation.cause();
        }

        System.out.println("Class: %s. %s".formatted(testSuiteClass.getSimpleName(), message));
    }

    private static void validateAndPrepareContainerAttributes(Method method, TestContainer container) {
        Method beforeSuite = determineSuiteMethod(method, BeforeSuite.class, container.getBeforeSuiteMethod());
        Method afterSuite = determineSuiteMethod(method, AfterSuite.class, container.getAfterSuiteMethod());

        if (beforeSuite != null) {
            container.setBeforeSuiteMethod(beforeSuite);
            return;
        }

        if (afterSuite != null) {
            container.setAfterSuiteMethod(afterSuite);
            return;
        }

        validateAnnotation(method);

        container.getTestMethods().add(method);
    }

    private static Method determineSuiteMethod(Method currentMethod, Class annotation, Method alreadyExistsMethod) {
        if (!currentMethod.isAnnotationPresent(annotation)) {
            return null;
        }

        if (alreadyExistsMethod != null) {
            throw new InvalidTestConfigurationException("Class: %s. Аннотация @%s встречается более 1 раза".formatted(
                    currentMethod.getDeclaringClass().getName(), annotation.getSimpleName()));
        }

        return currentMethod;
    }

    private static void validateAnnotation(Method method) {
        if (method.isAnnotationPresent(Test.class)) {
            validateTestAnnotation(method);
        }

        validateConcurrentUse(method);
    }

    private static void validateTestAnnotation(Method method) {
        Test testAnnotation = method.getAnnotation(Test.class);

        if (testAnnotation.priority() < 1 || testAnnotation.priority() > 10) {
            throw new InvalidTestConfigurationException("Некорректно задан priority аннотации @Test в классе %s".formatted(
                    method.getDeclaringClass().getSimpleName()));
        }
    }

    private static void validateConcurrentUse(Method method) {
        if (method.isAnnotationPresent(Test.class) &&
            (method.isAnnotationPresent(BeforeSuite.class) || method.isAnnotationPresent(AfterSuite.class))) {
            throw new InvalidTestConfigurationException(
                    "Class: %s. Аннотация @Test не может применяться вместе с аннотациями @BeforeSuite или @AfterSuite".formatted(
                            method.getDeclaringClass().getName()));
        }

        if (method.isAnnotationPresent(BeforeSuite.class) && method.isAnnotationPresent(AfterSuite.class)) {
            throw new InvalidTestConfigurationException(
                    "Class: %s. Аннотации @BeforeSuite и @AfterSuite не могут применяться вместе".formatted(
                            method.getDeclaringClass().getName()));
        }
    }

    private static List<TestStatistic> invoke(TestContainer container) {
        List<TestStatistic> testStatistic = new ArrayList<>();
        container.getTestMethods().sort(Comparator.comparingInt((Method m) -> m.getAnnotation(Test.class).priority()).reversed());

        if (container.getBeforeSuiteMethod() != null) {
            invokeSuiteMethod(container.getBeforeSuiteMethod());
        }

        for (Method testMethod : container.getTestMethods()) {
            try {
                if (testMethod.isAnnotationPresent(Disabled.class)) {
                    testStatistic.add(new TestStatistic(testMethod, TestResult.DISABLED));
                    continue;
                }

                testMethod.invoke(null);
                testStatistic.add(new TestStatistic(testMethod, TestResult.SUCCESS));
            } catch (Exception e) {
                testStatistic.add(new TestStatistic(testMethod, TestResult.ERROR));
            }
        }

        if (container.getAfterSuiteMethod() != null) {
            invokeSuiteMethod(container.getAfterSuiteMethod());
        }

        return testStatistic;
    }

    private static void invokeSuiteMethod(Method suiteMethod) {
        try {
            suiteMethod.invoke(null);
        } catch (Exception e) {
            throw new InvalidOperationException("Class: %s. Ошибка выполнения метода %s: %s".formatted(
                    suiteMethod.getDeclaringClass().getName(), suiteMethod.getName(), e.getLocalizedMessage()));
        }
    }
}
