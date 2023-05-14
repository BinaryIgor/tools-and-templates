package io.codyn.app.template.auth.api;

import io.codyn.app.template._common.core.model.UserRole;
import io.codyn.app.template._common.core.model.UserState;

import java.util.Set;
import java.util.UUID;

public record UserAuthData(UUID id,
                           UserState state,
                           Set<UserRole> roles) {

    public AuthenticatedUser toAuthenticatedUser() {
        return new AuthenticatedUser(id, state, roles);
    }
}
