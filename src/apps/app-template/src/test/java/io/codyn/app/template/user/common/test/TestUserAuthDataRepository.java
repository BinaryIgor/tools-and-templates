package io.codyn.app.template.user.common.test;

import io.codyn.app.template.auth.api.UserAuthData;
import io.codyn.app.template.auth.api.UserAuthDataRepository;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class TestUserAuthDataRepository implements UserAuthDataRepository {

    private final Map<UUID, UserAuthData> usersData = new HashMap<>();

    public void addUserData(UserAuthData userAuthData) {
        usersData.put(userAuthData.id(), userAuthData);
    }

    public void removeUserData(UUID id) {
        usersData.remove(id);
    }

    @Override
    public Optional<UserAuthData> ofId(UUID id) {
        return Optional.ofNullable(usersData.get(id));
    }
}
