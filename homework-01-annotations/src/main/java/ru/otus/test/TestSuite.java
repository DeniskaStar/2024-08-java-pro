package ru.otus.test;

import ru.otus.annotation.AfterSuite;
import ru.otus.annotation.BeforeSuite;
import ru.otus.annotation.Disabled;
import ru.otus.annotation.Test;

public class TestSuite {

    @BeforeSuite
    public static void init() {
        System.out.println("инициализация перед тестами прошла успешно");
    }

    @Test(priority = 7)
    public static void testFirst() {
        System.out.println("Первый тест выполнен");
    }

    @Test
    public static void testSecond() {
        System.out.println("Второй тест выполнен");
    }

    @Test(priority = 7)
    public static void testThird() {
        System.out.println("Третий тест выполнен");
    }

    @Disabled
    @Test
    public static void testFourth() {
        System.out.println("Четвертый тест выполнен");
    }

    @Test(priority = 10)
    public static void testFifth() {
        throw new RuntimeException("Ошибка при выполнении пятого теста");
    }

    @AfterSuite
    public static void cleanup() {
        System.out.println("Закрытие ресурсов после тестов прошло успешно");
    }
}
