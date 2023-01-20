package io.codyn.app.template.user.core.model;

import java.util.UUID;

public record NewUserRequest(UUID id,
                             String name,
                             String email,
                             String password) {

    public NewUserRequest(String name, String email, String password) {
        this(UUID.randomUUID(), name, email, password);
    }
}
