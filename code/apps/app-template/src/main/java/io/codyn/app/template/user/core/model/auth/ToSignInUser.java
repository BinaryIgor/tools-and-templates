package io.codyn.app.template.user.core.model.auth;

import io.codyn.app.template._shared.core.model.UserRoles;
import io.codyn.app.template._shared.core.model.UserState;

import java.util.UUID;

public record ToSignInUser(UUID id,
                           String name,
                           String email,
                           UserState state,
                           String password,
                           boolean secondFactorAuthentication,
                           UserRoles roles) {
}
