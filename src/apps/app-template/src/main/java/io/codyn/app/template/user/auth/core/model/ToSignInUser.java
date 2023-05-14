package io.codyn.app.template.user.auth.core.model;

import io.codyn.app.template._common.core.model.UserRoles;
import io.codyn.app.template._common.core.model.UserState;

import java.util.UUID;

public record ToSignInUser(UUID id,
                           String name,
                           String email,
                           UserState state,
                           String password,
                           boolean secondFactorAuthentication,
                           UserRoles roles) {
}
