package io.codyn.app.template._shared.core.exception;

public class ValidationException extends AppException {

    public ValidationException(String message) {
        super(message);
    }

    public static ValidationException ofField(String field, String value) {
        return new ValidationException("%s is not a valid %s".formatted(value, field));
    }
}
