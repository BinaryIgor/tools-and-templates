package io.codyn.app.template._shared.domain;

public class ValidationException extends RuntimeException {

    public ValidationException(String message) {
        super(message);
    }
}
