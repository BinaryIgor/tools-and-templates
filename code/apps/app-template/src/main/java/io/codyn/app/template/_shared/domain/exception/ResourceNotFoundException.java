package io.codyn.app.template._shared.domain.exception;

public class ResourceNotFoundException extends AppException {

    public ResourceNotFoundException(String message) {
        super(message);
    }

    public static ResourceNotFoundException ofId(String resource, Object id) {
        return new ResourceNotFoundException("%s of %s id doesn't exist".formatted(resource, id));
    }
}
