package io.codyn.app.processor.template.user.test;

import io.codyn.app.processor.template.user.core.UserState;

import java.util.UUID;

public record User(UUID id,
                   String name,
                   String email,
                   String password,
                   UserState state,
                   boolean secondFactorAuth) {

    public static User newUser(UUID id,
                               String name,
                               String email,
                               String password) {
        return new User(id, name, email, password, UserState.CREATED, false);
    }
}
