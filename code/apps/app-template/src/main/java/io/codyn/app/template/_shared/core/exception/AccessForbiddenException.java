package io.codyn.app.template._shared.core.exception;

public class AccessForbiddenException extends AppException {

    public AccessForbiddenException(String message) {
        super(message);
    }

    public AccessForbiddenException() {
        this("Current user doesn't have access to requested resource");
    }
}
