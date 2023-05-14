package io.codyn.app.template.user.auth.core.model;

import java.util.UUID;

public record CreateUserCommand(UUID id,
                                String name,
                                String email,
                                String password) {

    public CreateUserCommand(String name, String email, String password) {
        this(UUID.randomUUID(), name, email, password);
    }
}
