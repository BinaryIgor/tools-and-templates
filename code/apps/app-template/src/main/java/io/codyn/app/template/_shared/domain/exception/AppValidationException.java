package io.codyn.app.template._shared.domain.exception;

public class AppValidationException extends AppException {

    public AppValidationException(String message) {
        super(message);
    }

    public static AppValidationException ofField(String field, String value) {
        return new AppValidationException("%s is not a valid %s".formatted(value, field));
    }
}
