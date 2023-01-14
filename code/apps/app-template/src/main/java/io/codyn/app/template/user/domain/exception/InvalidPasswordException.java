package io.codyn.app.template.user.domain.exception;

import io.codyn.app.template._shared.domain.exception.CustomException;

public class InvalidPasswordException extends CustomException {

    public InvalidPasswordException() {
        super("Invalid password");
    }
}
