package io.codyn.app.template.user.auth.test;

import io.codyn.app.template._common.core.model.UserRole;
import io.codyn.app.template.user.common.core.repository.UserAuthRepository;

import java.util.*;

public class TestUserAuthRepository implements UserAuthRepository {

    private final Map<UUID, Collection<UserRole>> usersRoles = new HashMap<>();

    public void addUserRoles(UUID id, Collection<UserRole> roles) {
        usersRoles.put(id, roles);
    }

    @Override
    public Collection<UserRole> rolesOfUser(UUID id) {
        return usersRoles.getOrDefault(id, List.of());
    }
}
