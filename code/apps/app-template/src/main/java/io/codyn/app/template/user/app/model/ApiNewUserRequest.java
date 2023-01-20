package io.codyn.app.template.user.app.model;

import io.codyn.app.template.user.core.model.NewUserRequest;

public record ApiNewUserRequest(String name,
                                String email,
                                String password) {

    public NewUserRequest toRequest() {
        return new NewUserRequest(name, email, password);
    }
}
