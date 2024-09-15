package ru.otus;

import ru.otus.handler.TestRunner;
import ru.otus.test.DisabledTestSuite;
import ru.otus.test.TestSuite;

public class Main {

    public static void main(String[] args) {
        TestRunner.run(TestSuite.class);
        TestRunner.run(DisabledTestSuite.class);
    }
}
