package io.codyn.app.template.user.domain.component;

public interface PasswordHasher {
    String hash(String password);
}
