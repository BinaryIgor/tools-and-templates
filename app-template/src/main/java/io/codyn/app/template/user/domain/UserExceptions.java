package io.codyn.app.template.user.domain;

import io.codyn.app.template._shared.domain.exception.AppResourceExistsException;

public class UserExceptions {

    public static final String EMAIL_TAKEN = "EMAIL_TAKEN";

    public static AppResourceExistsException emailTaken() {
        return new AppResourceExistsException("User of %s email exists", EMAIL_TAKEN);
    }
}
