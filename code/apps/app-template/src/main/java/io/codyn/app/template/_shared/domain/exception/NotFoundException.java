package io.codyn.app.template._shared.domain.exception;

public class NotFoundException extends CustomException {

    public NotFoundException(String message) {
        super(message);
    }

    public static NotFoundException ofId(String resource, Object id) {
        return new NotFoundException("%s of %s id doesn't exist".formatted(resource, id));
    }
}
