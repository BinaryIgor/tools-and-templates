package io.codyn.app.template.user.core.repository;

import io.codyn.app.template._shared.core.model.UserRole;

import java.util.Collection;
import java.util.UUID;

public interface UserAuthRepository {

    Collection<UserRole> rolesOfUser(UUID id);
}
