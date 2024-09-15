package ru.otus.exception;

public class InvalidTestConfigurationException extends RuntimeException {

    public InvalidTestConfigurationException(String message) {
        super(message);
    }
}
