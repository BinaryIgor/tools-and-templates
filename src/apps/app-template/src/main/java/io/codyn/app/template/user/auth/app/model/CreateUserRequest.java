package io.codyn.app.template.user.auth.app.model;


import io.codyn.app.template.user.auth.core.model.CreateUserCommand;

public record CreateUserRequest(String name,
                                String email,
                                String password) {

    public CreateUserCommand toCommand() {
        return new CreateUserCommand(name, email, password);
    }
}
