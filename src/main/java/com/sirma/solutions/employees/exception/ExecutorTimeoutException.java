package com.sirma.solutions.employees.exception;

public class ExecutorTimeoutException extends RuntimeException{
    public ExecutorTimeoutException(String message) {
        super(message);
    }
}
