package io.codyn.app.template.user.auth.app.model;

import io.codyn.app.template.user.auth.core.model.NewUserRequest;

public record ApiNewUserRequest(String name,
                                String email,
                                String password) {

    public NewUserRequest toRequest() {
        return new NewUserRequest(name, email, password);
    }
}
