package io.codyn.app.template.user.common.test;

import io.codyn.app.template.user.auth.core.model.NewUserRequest;
import io.codyn.app.template.user.common.core.model.User;

public class TestUserMapper {

    public static NewUserRequest toNewUserRequest(User user) {
        return new NewUserRequest(user.id(), user.name(), user.email(), user.password());
    }
}