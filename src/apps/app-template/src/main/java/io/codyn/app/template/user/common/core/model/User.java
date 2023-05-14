package io.codyn.app.template.user.common.core.model;

import io.codyn.app.template._common.core.model.UserState;

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
