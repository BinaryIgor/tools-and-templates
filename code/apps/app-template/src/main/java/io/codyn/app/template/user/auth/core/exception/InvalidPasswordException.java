package io.codyn.app.template.user.auth.core.exception;

import io.codyn.app.template._common.core.exception.AppException;

public class InvalidPasswordException extends AppException {

    public InvalidPasswordException() {
        super("Invalid password");
    }
}
