package io.codyn.app.template._shared.domain.exception;

public class UnauthenticatedException extends AppException {

    public UnauthenticatedException() {
        super("Authentication required");
    }
}
