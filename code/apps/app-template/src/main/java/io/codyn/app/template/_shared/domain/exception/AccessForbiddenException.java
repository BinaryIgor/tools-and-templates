package io.codyn.app.template._shared.domain.exception;

public class AccessForbiddenException extends CustomException {

    public AccessForbiddenException(String message) {
        super(message);
    }

    public AccessForbiddenException() {
        this("Current user doesn't have access to requested resource");
    }
}
