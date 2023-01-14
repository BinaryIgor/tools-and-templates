package io.codyn.app.template.user.domain.repository;

import io.codyn.app.template._shared.domain.model.UserRole;

import java.util.Collection;
import java.util.UUID;

public interface UserAuthRepository {

    Collection<UserRole> rolesOfUser(UUID id);
}
