package io.codyn.app.template.user.domain.repository;

import io.codyn.app.template._shared.domain.model.UserState;

import java.util.UUID;

public interface UserUpdateRepository {
    void updateState(UUID id, UserState state);
}
