package io.codyn.app.template._shared.domain.exception;

public class AppOptimisticLockException extends AppException {

    public AppOptimisticLockException() {
        super("Trying to save outdated entity");
    }
}
