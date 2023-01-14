package io.codyn.app.template.user.test.repository;

import io.codyn.app.template._shared.domain.model.UserState;
import io.codyn.app.template.user.domain.model.User;
import io.codyn.app.template.user.domain.repository.UserRepository;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class TestUserRepository implements UserRepository {

    private final Map<String, User> usersByEmails = new HashMap<>();
    public User createdUser;

    public void addUser(User user) {
        usersByEmails.put(user.email(), user);
    }

    @Override
    public UUID create(User user) {
        createdUser = user;
        usersByEmails.put(user.email(), user);
        return user.id();
    }

    @Override
    public Optional<User> ofEmail(String email) {
        return Optional.ofNullable(usersByEmails.get(email));
    }

    public void changeUserState(String email, UserState state) {
        ofEmail(email)
                .ifPresent(u -> {
                    var updated = new User(createdUser.id(), createdUser.name(), createdUser.email(),
                            createdUser.password(), state, createdUser.secondFactorAuth());

                    usersByEmails.put(email, updated);
                });
    }
}
