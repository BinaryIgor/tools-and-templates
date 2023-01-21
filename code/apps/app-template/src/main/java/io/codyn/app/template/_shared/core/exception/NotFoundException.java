package io.codyn.app.template._shared.core.exception;

public class NotFoundException extends AppException {

    public NotFoundException(String message) {
        super(message);
    }

    public static NotFoundException ofId(String resource, Object id) {
        return new NotFoundException("%s of %s id doesn't exist".formatted(resource, id));
    }
}
