package io.codyn.system.monitor._shared.app;

public record ExceptionResponse(String error, String message) {

    public ExceptionResponse(Throwable exception) {
        this(exception.getClass().getSimpleName(), exception.getMessage());
    }
}
