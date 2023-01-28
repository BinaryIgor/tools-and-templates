package io.codyn.app.template.user.auth.core.model;

import io.codyn.app.template._common.core.model.UserRole;
import io.codyn.app.template._common.core.model.UserState;

import java.util.Collection;
import java.util.UUID;

public record CurrentUserData(UUID id,
                              String email,
                              String name,
                              UserState state,
                              Collection<UserRole> roles) {
}
