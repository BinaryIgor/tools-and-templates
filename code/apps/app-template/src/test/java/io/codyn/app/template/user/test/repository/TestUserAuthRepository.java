package io.codyn.app.template.user.test.repository;

import io.codyn.app.template._shared.core.model.UserRole;
import io.codyn.app.template.user.core.repository.UserAuthRepository;

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
