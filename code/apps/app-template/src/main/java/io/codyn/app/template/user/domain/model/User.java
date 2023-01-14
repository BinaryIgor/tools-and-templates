package io.codyn.app.template.user.domain.model;

import io.codyn.app.template._shared.domain.model.UserState;

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
