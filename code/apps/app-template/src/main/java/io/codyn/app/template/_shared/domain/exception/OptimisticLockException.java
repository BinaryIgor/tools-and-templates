package io.codyn.app.template._shared.domain.exception;

public class OptimisticLockException extends AppException {

    public OptimisticLockException(String entity) {
        super("Trying to save outdated %s entity".formatted(entity));
    }
}
