package io.codyn.app.template.auth.app;

import io.codyn.app.template._common.app.HttpRequestAttributes;
import io.codyn.app.template.auth.api.AuthenticatedUser;

import java.util.Optional;

public class AuthenticatedUserRequestHolder {

    public static void set(AuthenticatedUser user) {
        HttpRequestAttributes.set(HttpRequestAttributes.USER_ATTRIBUTE, user);
        HttpRequestAttributes.set(HttpRequestAttributes.USER_ID_ATTRIBUTE, user.id().toString());
    }

    public static Optional<AuthenticatedUser> get() {
        return HttpRequestAttributes.get(HttpRequestAttributes.USER_ATTRIBUTE, AuthenticatedUser.class);
    }
}
