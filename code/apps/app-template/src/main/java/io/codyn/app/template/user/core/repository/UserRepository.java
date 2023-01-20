package io.codyn.app.template.user.core.repository;

import io.codyn.app.template.user.core.model.User;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository {

    UUID create(User user);

    Optional<User> ofEmail(String email);
}
