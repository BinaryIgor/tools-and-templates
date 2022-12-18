package io.codyn.app.template.user.domain;

public interface PasswordEncoder {
    String encode(String password);
}
