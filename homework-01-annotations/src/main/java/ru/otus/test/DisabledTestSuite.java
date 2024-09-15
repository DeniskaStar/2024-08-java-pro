package ru.otus.test;

import ru.otus.annotation.BeforeSuite;
import ru.otus.annotation.Disabled;
import ru.otus.annotation.Test;

@Disabled(cause = "Тесты отключены по собственному желанию")
public class DisabledTestSuite {

    @BeforeSuite
    public static void init() {
        System.out.println("Инициализация перед тестами прошла успешно");
    }

    @Test
    public void testFirst() {
        System.out.println("Первый тест выполнен");
    }
}
