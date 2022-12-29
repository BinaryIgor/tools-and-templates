package io.codyn.app.template.user.test;

import io.codyn.app.template.test.Tests;
import io.codyn.app.template.user.domain.model.NewUser;
import io.codyn.app.template.user.domain.model.User;
import io.codyn.app.template.user.domain.model.UserState;

import java.util.List;
import java.util.UUID;

public class TestUserObjects {

    private static final List<User> USERS = List.of(
            new User(UUID.randomUUID(), "first-user", "first-user@gmail.com",
                    "ComplicatedPassword123", UserState.CREATED),
            new User(UUID.randomUUID(), "second-user", "second-user@gmail.com",
                    "ComplicatedPassword12", UserState.ACTIVATED),
            new User(UUID.randomUUID(), "third-user", "third-user@yahoo.com",
                    "PassComplicated99", UserState.ONBOARDED));

    public static List<User> users() {
        return USERS;
    }

    public static User user() {
        return Tests.randomElement(USERS);
    }

    public static NewUser newUser() {
        return TestUserMapper.toNewUser(user());
    }
}
