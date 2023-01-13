package io.codyn.app.template.user.domain.exception;

import io.codyn.app.template._shared.domain.exception.AppException;

public class InvalidPasswordException extends AppException {

    public InvalidPasswordException() {
        super("Invalid password");
    }
}
