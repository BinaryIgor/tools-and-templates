package io.codyn.app.template._shared.domain.exception;

public class ResourceForbiddenException extends AppException {

    public ResourceForbiddenException(String message) {
        super(message);
    }

    public ResourceForbiddenException() {
        this("Current user doesn't have access to requested resource");
    }
}
