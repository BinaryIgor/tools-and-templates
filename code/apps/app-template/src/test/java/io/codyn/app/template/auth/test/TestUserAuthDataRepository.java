package io.codyn.app.template.auth.test;

import io.codyn.app.template.auth.api.UserAuthData;
import io.codyn.app.template.auth.api.UserAuthDataRepository;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class TestUserAuthDataRepository implements UserAuthDataRepository {

    private final Map<UUID, UserAuthData> usersAuthData = new HashMap<>();

    public void addUserData(UserAuthData data) {
        usersAuthData.put(data.id(), data);
    }

    @Override
    public Optional<UserAuthData> ofId(UUID id) {
        return Optional.ofNullable(usersAuthData.get(id));
    }
}
