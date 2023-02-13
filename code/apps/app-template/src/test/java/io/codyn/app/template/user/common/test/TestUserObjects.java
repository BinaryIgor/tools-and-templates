package io.codyn.app.template.user.common.test;

import io.codyn.app.template._common.core.model.UserState;
import io.codyn.app.template._common.core.validator.FieldValidator;
import io.codyn.app.template.user.auth.core.model.CreateUserCommand;
import io.codyn.app.template.user.common.core.model.ActivationToken;
import io.codyn.app.template.user.common.core.model.ActivationTokenType;
import io.codyn.app.template.user.common.core.model.User;
import io.codyn.test.TestRandom;

import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

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


    public static User user() {
        return TestRandom.oneOf(USERS);
    }

    public static CreateUserCommand createUserCommand() {
        return TestUserMapper.toCreateUserCommand(user());
    }

    public static CreateUserCommand createUserCommand1() {
        return TestUserMapper.toCreateUserCommand(USERS.get(0));
    }

    public static CreateUserCommand createUserCommand2() {
        return TestUserMapper.toCreateUserCommand(USERS.get(1));
    }

    public static ActivationToken activationToken(UUID userId) {
        return activationToken(userId, TestRandom.oneOf(ActivationTokenType.values()));
    }

    public static ActivationToken activationToken(UUID userId, ActivationTokenType type) {
        return new ActivationToken(userId, type, TestRandom.string(), TestRandom.instant());
    }

    public static List<String> invalidEmails() {
        var tooLongEmailHandle = "x".repeat(FieldValidator.MAX_EMAIL_LENGTH);
        var tooLongEmail = "%s@gmail.com".formatted(tooLongEmailHandle);

        return Stream.of("", null, "_@gmail.com", "@gmail.com", "email@e.", "email@exx", tooLongEmail)
                .toList();
    }

    public static List<String> invalidPasswords() {
        var tooLongPassword = "x1A".repeat(FieldValidator.MAX_PASSWORD_LENGTH / 3 + 1);
        return Stream.of("", null, " ", "onlycharacters", "123456789", "Short1", tooLongPassword)
                .toList();
    }

    public static List<String> invalidActivationTokens() {
        return Stream.of(" ", null, TestRandom.string()).toList();
    }
}
