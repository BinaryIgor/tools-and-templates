package io.codyn.app.template.user.auth.core.exception;

import io.codyn.app.template._common.core.exception.AppException;

public class NotMatchedPasswordException extends AppException {

    public NotMatchedPasswordException() {
        super("Password is not matching");
    }
}
