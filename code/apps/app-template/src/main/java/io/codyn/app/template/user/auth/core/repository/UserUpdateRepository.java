package io.codyn.app.template.user.auth.core.repository;

import io.codyn.app.template._common.core.model.UserState;

import java.util.UUID;

public interface UserUpdateRepository {
    void updateState(UUID id, UserState state);
}
