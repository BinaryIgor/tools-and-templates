package io.codyn.app.template.user.domain.model.auth;

import io.codyn.app.template._shared.domain.model.UserRoles;
import io.codyn.app.template._shared.domain.model.UserState;

import java.util.UUID;

public record ToSignInUser(UUID id,
                           String name,
                           String email,
                           UserState state,
                           String password,
                           boolean secondFactorAuthentication,
                           UserRoles roles) {
}
