package io.codyn.app.template._shared.domain.exception;

public class UnauthenticatedException extends CustomException {

    public UnauthenticatedException() {
        super("Authentication required");
    }
}
