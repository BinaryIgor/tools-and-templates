package io.codyn.app.template.user.test;

import io.codyn.app.template.user.domain.model.NewUser;
import io.codyn.app.template.user.domain.model.User;

public class TestUserMapper {

    public static NewUser toNewUser(User user) {
        return new NewUser(user.name(), user.email(), user.password());
    }
}
