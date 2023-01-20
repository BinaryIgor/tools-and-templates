package io.codyn.app.template.user.core.repository;

import io.codyn.app.template._shared.core.model.UserState;

import java.util.UUID;

public interface UserUpdateRepository {
    void updateState(UUID id, UserState state);
}
