package io.codyn.app.template.user.test.repository;

import io.codyn.app.template._shared.core.model.UserState;
import io.codyn.app.template.user.core.repository.UserUpdateRepository;
import io.codyn.types.Pair;

import java.util.UUID;

public class TestUserUpdateRepository implements UserUpdateRepository {

    public Pair<UUID, UserState> updatedState;

    @Override
    public void updateState(UUID id, UserState state) {
        updatedState = new Pair<>(id, state);
    }
}
