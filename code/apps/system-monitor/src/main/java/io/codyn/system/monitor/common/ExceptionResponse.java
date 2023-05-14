package io.codyn.system.monitor.common;

public record ExceptionResponse(String error, String message) {

    public ExceptionResponse(Throwable exception) {
        this(exception.getClass().getSimpleName(), exception.getMessage());
    }
}
