package io.codyn.app.template._common.core.exception;

public class UnauthenticatedException extends AppException {

    public UnauthenticatedException() {
        super("Authentication required");
    }
}
