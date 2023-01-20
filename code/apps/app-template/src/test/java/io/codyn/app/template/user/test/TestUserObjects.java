package io.codyn.app.template.user.test;

import io.codyn.app.template._shared.core.model.UserState;
import io.codyn.app.template.user.core.model.NewUserRequest;
import io.codyn.app.template.user.core.model.User;
import io.codyn.app.template.user.core.model.activation.ActivationToken;
import io.codyn.app.template.user.core.model.activation.ActivationTokenType;
import io.codyn.test.TestRandom;

import java.util.List;
import java.util.UUID;

public class TestUserObjects {

    private static final List<User> USERS = List.of(
            new User(UUID.randomUUID(), "first-user", "first-user@gmail.com",
                    "ComplicatedPassword123", UserState.CREATED, false),
            new User(UUID.randomUUID(), "second-user", "second-user@gmail.com",
                    "ComplicatedPassword12", UserState.ACTIVATED, false),
            new User(UUID.randomUUID(), "third-user", "third-user@yahoo.com",
                    "PassComplicated99", UserState.ONBOARDED, true));

    public static List<User> users() {
        return USERS;
    }

    public static User user1() {
        return USERS.get(0);
    }

    public static User user2() {
        return USERS.get(1);
    }

    public static List<NewUserRequest> newUserRequests() {
        return users().stream().map(TestUserMapper::toNewUserRequest).toList();
    }

    public static User user() {
        return TestRandom.oneOf(USERS);
    }

    public static NewUserRequest newUserRequest() {
        return TestUserMapper.toNewUserRequest(user());
    }

    public static NewUserRequest newUserRequest1() {
        return TestUserMapper.toNewUserRequest(USERS.get(0));
    }

    public static NewUserRequest newUserRequest2() {
        return TestUserMapper.toNewUserRequest(USERS.get(1));
    }

    public static ActivationToken activationToken(UUID userId) {
        return activationToken(userId, TestRandom.oneOf(ActivationTokenType.values()));
    }

    public static ActivationToken activationToken(UUID userId, ActivationTokenType type) {
        return new ActivationToken(userId, type, TestRandom.string(), TestRandom.instant());
    }
}
