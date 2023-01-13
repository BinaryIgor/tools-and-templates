package io.codyn.app.template._shared.test;

import io.codyn.app.template.user.domain.component.PasswordHasher;

public class TestPasswordHasher implements PasswordHasher {

    @Override
    public String hash(String password) {
        return "test_hash-" + password;
    }

    @Override
    public boolean matches(String rawPassword, String hashedPassword) {
        return hash(rawPassword).equals(hashedPassword);
    }
}
