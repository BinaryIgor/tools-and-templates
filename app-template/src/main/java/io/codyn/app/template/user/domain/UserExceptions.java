package io.codyn.app.template.user.domain;

import io.codyn.app.template._shared.domain.exception.ResourceExistsException;

public class UserExceptions {

    public static final String EMAIL_TAKEN = "EMAIL_TAKEN";

    public static ResourceExistsException emailTaken() {
        return new ResourceExistsException("User of %s email exists", EMAIL_TAKEN);
    }
}
