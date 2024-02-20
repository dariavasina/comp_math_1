package org.example;

public class SolverException extends Exception{
    public SolverException(String message) {
        super(message);
    }

    public SolverException(String message, Throwable cause) {
        super(message, cause);
    }
}
