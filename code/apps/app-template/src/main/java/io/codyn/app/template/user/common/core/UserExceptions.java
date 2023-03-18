package io.codyn.app.template.user.common.core;

import io.codyn.app.template._common.core.exception.NotFoundException;
import io.codyn.app.template.user.common.core.model.ActivationTokenId;

import java.util.UUID;

public class UserExceptions {

    public static NotFoundException activationTokenNotFound(ActivationTokenId id) {
        return NotFoundException.ofId("ActivationToken", id);
    }

    public static NotFoundException userOfEmailNotFound(String email) {
        return new NotFoundException("User of %s email doesn't exist".formatted(email));
    }

    public static NotFoundException userOfIdNotFound(UUID id) {
        return new NotFoundException("User of %s id doesn't exist".formatted(id));
    }
}
