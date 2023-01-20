package io.codyn.app.template.user.core.model;

import io.codyn.app.template._shared.core.model.UserRole;
import io.codyn.app.template._shared.core.model.UserState;

import java.util.Collection;
import java.util.UUID;

public record CurrentUserData(UUID id,
                              String email,
                              String name,
                              UserState state,
                              Collection<UserRole> roles) {
}
