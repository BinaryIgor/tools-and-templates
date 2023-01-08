package io.codyn.app.template.auth.api;

import io.codyn.app.template._shared.domain.model.UserRole;
import io.codyn.app.template._shared.domain.model.UserState;

import java.util.Set;
import java.util.UUID;

public record UserAuthData(UUID id,
                           UserState state,
                           Set<UserRole> roles) {
}
