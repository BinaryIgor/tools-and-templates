package io.codyn.app.template.user.domain.model;

import java.util.Collection;
import java.util.UUID;

public record CurrentUserData(UUID id,
                              String email,
                              String name,
                              UserState state,
                              Collection<UserRole> roles) {
}
