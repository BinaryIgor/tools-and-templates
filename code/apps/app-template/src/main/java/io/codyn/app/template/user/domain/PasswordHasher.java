package io.codyn.app.template.user.domain;

public interface PasswordHasher {
    String hash(String password);
}
