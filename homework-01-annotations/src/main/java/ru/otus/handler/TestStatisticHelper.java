package ru.otus.handler;

import ru.otus.model.TestResult;
import ru.otus.model.TestStatistic;

import java.util.List;

public class TestStatisticHelper {

    public static void process(List<TestStatistic> statistics, Class<?> testSuiteClass) {
        System.out.println("Статистика выполнения тестов класса %s:".formatted(testSuiteClass.getSimpleName()));

        long countSuccessTests = getCountByStatus(statistics, TestResult.SUCCESS);
        long countErrorTests = getCountByStatus(statistics, TestResult.ERROR);
        long countDisabledTests = getCountByStatus(statistics, TestResult.DISABLED);

        System.out.println("Всего тестов: %d".formatted(statistics.size()));
        System.out.println("Успешно пройденых тестов: %d".formatted(countSuccessTests));
        System.out.println("Непройденных тестов: %d".formatted(countErrorTests));
        System.out.println("Пропущенных тестов: %d".formatted(countDisabledTests));
    }

    private static long getCountByStatus(List<TestStatistic> statistics, TestResult resultStatus) {
        return statistics.stream()
                .filter(it -> it.getTestResult() == resultStatus)
                .count();
    }
}
