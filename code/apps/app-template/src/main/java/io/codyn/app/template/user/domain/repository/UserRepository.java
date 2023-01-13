package io.codyn.app.template.user.domain.repository;

import io.codyn.app.template.user.domain.model.User;

import java.util.Optional;

public interface UserRepository {
    Optional<User> ofEmail(String email);
}
