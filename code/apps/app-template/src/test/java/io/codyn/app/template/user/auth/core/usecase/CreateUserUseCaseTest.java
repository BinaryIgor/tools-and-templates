package io.codyn.app.template.user.auth.core.usecase;

import io.codyn.app.template._common.core.exception.*;
import io.codyn.app.template._common.core.validator.FieldValidator;
import io.codyn.app.template._common.test.EmailAssertions;
import io.codyn.app.template._common.test.TestEmailServer;
import io.codyn.app.template._common.test.TestPasswordHasher;
import io.codyn.app.template.user.auth.core.model.CreateUserCommand;
import io.codyn.app.template.user.auth.test.TestUserRepository;
import io.codyn.app.template.user.common.core.ActivationTokenData;
import io.codyn.app.template.user.common.core.ActivationTokenFactory;
import io.codyn.app.template.user.common.core.ActivationTokens;
import io.codyn.app.template.user.common.core.UserEmailSender;
import io.codyn.app.template.user.common.core.model.*;
import io.codyn.app.template.user.common.test.*;
import io.codyn.email.model.Email;
import io.codyn.test.TestClock;
import io.codyn.test.TestRandom;
import io.codyn.test.TestTransactions;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.time.Duration;
import java.util.stream.Stream;

public class CreateUserUseCaseTest {

    private CreateUserUseCase useCase;
    private TestUserRepository userRepository;
    private TestPasswordHasher passwordHasher;
    private TestEmailServer emailServer;
    private UserEmailSender userEmailSender;
    private TestActivationTokenRepository activationTokenRepository;
    private TestTokenFactory tokenFactory;
    private TestClock clock;
    private TestTransactions transactions;

    @BeforeEach
    void setup() {
        userRepository = new TestUserRepository();
        passwordHasher = new TestPasswordHasher();
        transactions = new TestTransactions();

        emailServer = new TestEmailServer();
        userEmailSender = TestUserEmailsProvider.sender(emailServer);

        activationTokenRepository = new TestActivationTokenRepository();

        tokenFactory = new TestTokenFactory();
        clock = new TestClock();

        var activationTokens = new ActivationTokens(activationTokenRepository,
                new ActivationTokenFactory(tokenFactory, clock));

        transactions = new TestTransactions();

        useCase = new CreateUserUseCase(userRepository, passwordHasher, activationTokens, userEmailSender,
                transactions);
    }

    @ParameterizedTest
    @MethodSource("invalidUserCases")
    void shouldThrowExceptionGivenInvalidUser(CreateUserCommand command,
                                              Exception exception) {
        Assertions.assertThatThrownBy(() -> useCase.handle(command))
                .isEqualTo(exception);
    }

    @Test
    void shouldThrowExceptionGivenUserWithTakenEmail() {
        var user = TestUserObjects.user();

        userRepository.addUser(user);

        Assertions.assertThatThrownBy(() -> useCase.handle(TestUserMapper.toCreateUserCommand(user)))
                .isEqualTo(new EmailTakenException(user.email()));
    }

    @Test
    void shouldThrowExceptionGivenUserWithUnreachableEmail() {
        var user = new CreateUserCommand("some-name", "some-name@unreacheable-xxx.com", "Password134");

        Assertions.assertThatThrownBy(() -> useCase.handle(user))
                .isEqualTo(new EmailNotReachableException(user.email()));
    }

    @Test
    void shouldCreateUserInTransaction() {
        var testCase = prepareCreatesUserTestCase();

        transactions.test()
                .before(() -> {
                    Assertions.assertThat(userRepository.createdUser).isNull();
                    Assertions.assertThat(activationTokenRepository.ofId(testCase.expectedActivationTokenId)).isEmpty();
                    Assertions.assertThat(emailServer.sentEmail).isNull();
                })
                .after(() -> {
                    Assertions.assertThat(userRepository.createdUser).isEqualTo(testCase.expectedUser);

                    var savedToken = activationTokenRepository.ofId(testCase.expectedActivationTokenId).orElseThrow();

                    Assertions.assertThat(savedToken)
                            .isEqualTo(testCase.expectedActivationToken);

                    var sentEmail = emailServer.sentEmail;

                    Assertions.assertThat(sentEmail).isEqualTo(testCase.expectedEmail);
                    EmailAssertions.messageContains(sentEmail, savedToken.token());
                })
                .execute(() -> useCase.handle(testCase.command));
    }

    static Stream<Arguments> invalidUserCases() {
        return Stream.concat(Stream.concat(invalidUserNameCases(), invalidUserEmailCases()),
                invalidUserPasswordCases());
    }

    static Stream<Arguments> invalidUserNameCases() {
        var tooLongName = "x".repeat(FieldValidator.MAX_NAME_LENGTH + 1);
        return Stream.of(" ", null, "", "a", "_*", tooLongName)
                .map(n -> {
                    var u = new CreateUserCommand(n, "email@email.com", "complicated-password");
                    return Arguments.of(u, new InvalidNameException(n));
                });
    }

    static Stream<Arguments> invalidUserEmailCases() {
        return TestUserObjects.invalidEmails().stream()
                .map(e -> {
                    var u = new CreateUserCommand("some-name", e, "password");
                    return Arguments.of(u, new InvalidEmailException(e));
                });
    }

    static Stream<Arguments> invalidUserPasswordCases() {
        return TestUserObjects.invalidPasswords().stream()
                .map(p -> {
                    var u = new CreateUserCommand("some-name", "some-email@gmail.com", p);
                    return Arguments.of(u, new InvalidPasswordException());
                });
    }

    private CreatesUserTestCase prepareCreatesUserTestCase() {
        var command = TestUserObjects.createUserCommand();
        var newUserId = command.id();

        var expectedUser = User.newUser(newUserId, command.name(), command.email(),
                passwordHasher.hash(command.password()));

        var expectedTokenId = ActivationTokenId.ofNewUser(newUserId);

        var token = tokenFactory.addNextToken(ActivationTokenData.withUserId(newUserId), TestRandom.string());
        var expectedActivationToken = new ActivationToken(newUserId, ActivationTokenType.NEW_USER, token,
                clock.instant().plus(Duration.ofMinutes(15)));

        userEmailSender.sendAccountActivation(new EmailUser(command.name(), command.email()), token);
        var expectedEmail = emailServer.sentEmail;
        emailServer.clear();

        return new CreatesUserTestCase(command, expectedUser,
                expectedTokenId, expectedActivationToken,
                expectedEmail);
    }

    private record CreatesUserTestCase(CreateUserCommand command,
                                       User expectedUser,
                                       ActivationTokenId expectedActivationTokenId,
                                       ActivationToken expectedActivationToken,
                                       Email expectedEmail) {

    }
}
