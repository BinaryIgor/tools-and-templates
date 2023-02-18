package io.codyn.app.template.user.common.core.repository;

import io.codyn.app.template._common.core.model.UserState;

import java.util.UUID;

public interface UserUpdateRepository {

    void updateEmail(UUID id, String email);

    void updateState(UUID id, UserState state);

    void updatePassword(UUID id, String password);
}
