package io.codyn.app.template.user.common.core;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

public class BcryptPasswordHasher implements PasswordHasher {

    private final PasswordEncoder encoder;

    public BcryptPasswordHasher(int strength) {
        encoder = new BCryptPasswordEncoder(strength);
    }

    public BcryptPasswordHasher() {
        this(10);
    }

    @Override
    public String hash(String password) {
        return encoder.encode(password);
    }

    @Override
    public boolean matches(String rawPassword, String hashedPassword) {
        return encoder.matches(rawPassword, hashedPassword);
    }
}
