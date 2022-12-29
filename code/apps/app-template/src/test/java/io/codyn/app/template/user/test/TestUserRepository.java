package io.codyn.app.template.user.test;

import io.codyn.app.template.user.domain.model.User;
import io.codyn.app.template.user.domain.repository.UserRepository;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class TestUserRepository implements UserRepository {

    private final Map<String, User> usersByEmails = new HashMap<>();

    public void addUser(User user) {
        usersByEmails.put(user.email(), user);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return Optional.ofNullable(usersByEmails.get(email));
    }
}
