package io.codyn.app.template.user.domain;

import io.codyn.app.template._shared.domain.exception.AppValidationException;
import io.codyn.app.template._shared.domain.validator.FieldValidator;
import io.codyn.app.template._shared.test.TestEventPublisher;
import io.codyn.app.template._shared.test.TestPasswordHasher;
import io.codyn.app.template.user.api.event.UserCreatedEvent;
import io.codyn.app.template.user.domain.model.NewUser;
import io.codyn.app.template.user.test.TestNewUserRepository;
import io.codyn.app.template.user.test.TestUserMapper;
import io.codyn.app.template.user.test.TestUserObjects;
import io.codyn.app.template.user.test.TestUserRepository;
import io.codyn.commons.test.TestTransactions;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.UUID;
import java.util.stream.Stream;

public class NewUserServiceTest {

    private NewUserService service;
    private TestNewUserRepository newUserRepository;
    private TestUserRepository userRepository;
    private TestPasswordHasher passwordHasher;
    private TestTransactions transactions;
    private TestEventPublisher eventPublisher;

    @BeforeEach
    void setup() {
        newUserRepository = new TestNewUserRepository();
        userRepository = new TestUserRepository();
        passwordHasher = new TestPasswordHasher();
        transactions = new TestTransactions();
        eventPublisher = new TestEventPublisher();

        service = new NewUserService(newUserRepository, userRepository, passwordHasher,
                transactions, eventPublisher);
    }

    @ParameterizedTest
    @MethodSource("invalidUserCases")
    void shouldThrowExceptionGivenInvalidUser(NewUser user,
                                              AppValidationException exception) {
        Assertions.assertThatThrownBy(() -> service.create(user))
                .isEqualTo(exception);
    }

    @Test
    void shouldThrowExceptionGivenUserWithTakenEmail() {
        var user = TestUserObjects.user();

        userRepository.addUser(user);

        Assertions.assertThatThrownBy(() -> service.create(TestUserMapper.toNewUser(user)))
                .isEqualTo(UserExceptions.emailTaken());
    }

    @Test
    void shouldCreateNewUserInTransaction() {
        var newUser = TestUserObjects.newUser();
        var newUserId = UUID.randomUUID();

        newUserRepository.nextId = newUserId;

        var expectedNewUser = newUser.withPassword(passwordHasher.hash(newUser.password()));
        var expectedEvent = new UserCreatedEvent(newUserId, newUser.name(), newUser.email());

        transactions.test()
                .before(() -> {
                    Assertions.assertThat(newUserRepository.createdUser).isNull();
                    Assertions.assertThat(eventPublisher.publishedEvent).isNull();
                })
                .after(() -> {
                    Assertions.assertThat(newUserRepository.createdUser).isEqualTo(expectedNewUser);
                    Assertions.assertThat(eventPublisher.publishedEvent).isEqualTo(expectedEvent);
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
                    var u = new NewUser(n, "email@email.com", "complicated-password");
                    return Arguments.of(u, AppValidationException.ofField("name", n));
                });
    }

    static Stream<Arguments> invalidUserEmailCases() {
        var tooLongEmailHandle = "x".repeat(FieldValidator.MAX_EMAIL_LENGTH);
        var tooLongEmail = "%s@gmail.com".formatted(tooLongEmailHandle);

        return Stream.of("", null, "_@gmail.com", "@gmail.com", "email@e.", "email@exx", tooLongEmail)
                .map(e -> {
                    var u = new NewUser("some-name", e, "password");
                    return Arguments.of(u, AppValidationException.ofField("email", e));
                });
    }

    static Stream<Arguments> invalidUserPasswordCases() {
        var tooLongPassword = "x1A".repeat(FieldValidator.MAX_PASSWORD_LENGTH / 3 + 1);

        return Stream.of("", null, " ", "onlycharacters", "123456789", "Short1", tooLongPassword)
                .map(p -> {
                    var u = new NewUser("some-name", "some-email@gmail.com", p);
                    return Arguments.of(u, AppValidationException.ofField("password", p));
                });
    }
}