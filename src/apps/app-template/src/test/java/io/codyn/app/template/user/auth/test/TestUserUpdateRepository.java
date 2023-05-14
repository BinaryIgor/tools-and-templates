package io.codyn.app.template.user.auth.test;

import io.codyn.app.template._common.core.model.UserState;
import io.codyn.app.template.user.common.core.repository.UserUpdateRepository;
import io.codyn.types.Pair;

import java.util.UUID;

public class TestUserUpdateRepository implements UserUpdateRepository {

    public Pair<UUID, String> updatedEmail;
    public Pair<UUID, UserState> updatedState;
    public Pair<UUID, String> updatedPassword;

    @Override
    public void updateEmail(UUID id, String email) {
        updatedEmail = new Pair<>(id, email);
    }

    @Override
    public void updateState(UUID id, UserState state) {
        updatedState = new Pair<>(id, state);
    }

    @Override
    public void updatePassword(UUID id, String password) {
        updatedPassword = new Pair<>(id, password);
    }
}
