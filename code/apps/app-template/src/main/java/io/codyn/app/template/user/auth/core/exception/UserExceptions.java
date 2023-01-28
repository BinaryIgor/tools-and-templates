package io.codyn.app.template.user.auth.core.exception;

import io.codyn.app.template._common.core.exception.NotFoundException;

public class UserExceptions {

    public static NotFoundException userOfEmailNotFound(String email) {
        return new NotFoundException("User of %s email doesn't exist".formatted(email));
    }
}
