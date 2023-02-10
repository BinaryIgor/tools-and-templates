package io.codyn.app.template.user.auth.core.service;

import io.codyn.app.template._common.core.exception.*;
import io.codyn.app.template._common.core.validator.FieldValidator;
import io.codyn.app.template._common.test.TestEventHandler;
import io.codyn.app.template._common.test.TestPasswordHasher;
import io.codyn.app.template.user.auth.core.event.UserCreatedEvent;
import io.codyn.app.template.user.auth.core.model.NewUserRequest;
import io.codyn.app.template.user.auth.test.TestUserRepository;
import io.codyn.app.template.user.common.core.model.User;
import io.codyn.app.template.user.common.test.TestUserMapper;
import io.codyn.app.template.user.common.test.TestUserObjects;
import io.codyn.test.TestTransactions;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

public class NewUserServiceTest {

    private NewUserService service;
    private TestUserRepository userRepository;
    private TestPasswordHasher passwordHasher;
    private TestTransactions transactions;
    private TestEventHandler<UserCreatedEvent> userCreatedEventHandler;

    @BeforeEach
    void setup() {
        userRepository = new TestUserRepository();
        passwordHasher = new TestPasswordHasher();
        transactions = new TestTransactions();
        userCreatedEventHandler = new TestEventHandler<>();

        service = new NewUserService(userRepository, passwordHasher,
                transactions, userCreatedEventHandler);
    }

    @ParameterizedTest
    @MethodSource("invalidUserCases")
    void shouldThrowExceptionGivenInvalidUser(NewUserRequest user,
                                              AppException exception) {
        Assertions.assertThatThrownBy(() -> service.create(user))
                .isEqualTo(exception);
    }

    @Test
    void shouldThrowExceptionGivenUserWithTakenEmail() {
        var user = TestUserObjects.user();

        userRepository.addUser(user);

        Assertions.assertThatThrownBy(() -> service.create(TestUserMapper.toNewUserRequest(user)))
                .isEqualTo(new EmailTakenException(user.email()));
    }

    @Test
    void shouldThrowExceptionGivenUserWithUnreachableEmail() {
        var user = new NewUserRequest("some-name", "some-name@unreacheable-xxx.com", "Password134");

        Assertions.assertThatThrownBy(() -> service.create(user))
                .isEqualTo(new EmailNotReachableException(user.email()));
    }

    @Test
    void shouldCreateNewUserInTransaction() {
        var newUser = TestUserObjects.newUserRequest();
        var newUserId = newUser.id();

        var expectedNewUser = User.newUser(newUserId, newUser.name(), newUser.email(),
                passwordHasher.hash(newUser.password()));
        var expectedEvent = new UserCreatedEvent(newUserId, newUser.name(), newUser.email());

        transactions.test()
                .before(() -> {
                    Assertions.assertThat(userRepository.createdUser).isNull();
                    Assertions.assertThat(userCreatedEventHandler.handledEvent).isNull();
                })
                .after(() -> {
                    Assertions.assertThat(userRepository.createdUser).isEqualTo(expectedNewUser);
                    Assertions.assertThat(userCreatedEventHandler.handledEvent).isEqualTo(expectedEvent);
                })
                .execute(() -> service.create(newUser));
    }

    static Stream<Arguments> invalidUserCases() {
        return Stream.concat(Stream.concat(invalidUserNameCases(), invalidUserEmailCases()),
                invalidUserPasswordCases());
    }

    static Stream<Arguments> invalidUserNameCases() {
        var tooLongName = "x".repeat(FieldValidator.MAX_NAME_LENGTH + 1);
        return Stream.of(" ", null, "", "a", "_*", tooLongName)
                .map(n -> {
                    var u = new NewUserRequest(n, "email@email.com", "complicated-password");
                    return Arguments.of(u, new InvalidNameException(n));
                });
    }

    static Stream<Arguments> invalidUserEmailCases() {
        var tooLongEmailHandle = "x".repeat(FieldValidator.MAX_EMAIL_LENGTH);
        var tooLongEmail = "%s@gmail.com".formatted(tooLongEmailHandle);

        return Stream.of("", null, "_@gmail.com", "@gmail.com", "email@e.", "email@exx", tooLongEmail)
                .map(e -> {
                    var u = new NewUserRequest("some-name", e, "password");
                    return Arguments.of(u, new InvalidEmailException(e));
                });
    }

    static Stream<Arguments> invalidUserPasswordCases() {
        var tooLongPassword = "x1A".repeat(FieldValidator.MAX_PASSWORD_LENGTH / 3 + 1);

        return Stream.of("", null, " ", "onlycharacters", "123456789", "Short1", tooLongPassword)
                .map(p -> {
                    var u = new NewUserRequest("some-name", "some-email@gmail.com", p);
                    return Arguments.of(u, new InvalidPasswordException());
                });
    }
}
