package io.codyn.app.template.user.common.core;

public interface PasswordHasher {

    String hash(String password);

    boolean matches(String rawPassword, String hashedPassword);
}
