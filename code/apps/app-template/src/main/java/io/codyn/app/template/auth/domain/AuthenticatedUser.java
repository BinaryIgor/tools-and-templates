package io.codyn.app.template.auth.domain;


import io.codyn.app.template._shared.domain.model.UserRole;
import io.codyn.app.template._shared.domain.model.UserRoles;
import io.codyn.app.template._shared.domain.model.UserState;

import java.util.Set;
import java.util.UUID;

public record AuthenticatedUser(UUID id,
                                UserState state,
                                UserRoles roles) {

    public AuthenticatedUser(UUID id, UserState state, Set<UserRole> roles) {
        this(id, state, UserRoles.of(roles));
    }

    public static AuthenticatedUser withoutRoles(UUID id, UserState state) {
        return new AuthenticatedUser(id, state, UserRoles.empty());
    }
}