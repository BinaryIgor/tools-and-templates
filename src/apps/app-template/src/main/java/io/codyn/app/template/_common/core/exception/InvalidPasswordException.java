package io.codyn.app.template._common.core.exception;

public class InvalidPasswordException extends AppException {

    public InvalidPasswordException() {
        super("Password is not valid");
    }
}
