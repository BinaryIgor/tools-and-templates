package io.codyn.app.template.user.core.exception;

import io.codyn.app.template._shared.core.exception.AppException;

public class InvalidPasswordException extends AppException {

    public InvalidPasswordException() {
        super("Invalid password");
    }
}
