package io.codyn.app.template.user.test;

import io.codyn.app.template.user.domain.model.NewUserRequest;
import io.codyn.app.template.user.domain.model.User;

public class TestUserMapper {

    public static NewUserRequest toNewUserRequest(User user) {
        return new NewUserRequest(user.id(), user.name(), user.email(), user.password());
    }
}
