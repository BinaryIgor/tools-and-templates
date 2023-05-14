package io.codyn.app.template.user.common.test;

import io.codyn.app.template.user.auth.core.model.CreateUserCommand;
import io.codyn.app.template.user.common.core.model.User;

public class TestUserMapper {

    public static CreateUserCommand toCreateUserCommand(User user) {
        return new CreateUserCommand(user.id(), user.name(), user.email(), user.password());
    }
}
