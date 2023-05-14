package io.codyn.app.template.user.auth.core.usecase;

import io.codyn.app.template._common.core.exception.InvalidPasswordException;
import io.codyn.app.template._common.test.TestPasswordHasher;
import io.codyn.app.template.user.auth.core.model.SetNewPasswordCommand;
import io.codyn.app.template.user.auth.test.TestUserUpdateRepository;
import io.codyn.app.template.user.common.core.ActivationTokenConsumer;
import io.codyn.app.template.user.common.core.ActivationTokenFactory;
import io.codyn.app.template.user.common.core.exception.InvalidActivationTokenException;
import io.codyn.app.template.user.common.core.model.ActivationTokenId;
import io.codyn.app.template.user.common.test.TestActivationTokenRepository;
import io.codyn.app.template.user.common.test.TestUserObjects;
import io.codyn.test.TestRandom;
import io.codyn.test.TestTransactions;
import io.codyn.types.Pair;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.time.Clock;
import java.util.List;
import java.util.UUID;

public class SetNewUserPasswordUseCaseTest {

    private SetNewUserPasswordUseCase useCase;
    private TestPasswordHasher passwordHasher;
    private TestActivationTokenRepository activationTokenRepository;
    private TestUserUpdateRepository userUpdateRepository;
    private TestTransactions transactions;
    private ActivationTokenFactory activationTokenFactory;

    @BeforeEach
    void setup() {
        passwordHasher = new TestPasswordHasher();

        activationTokenRepository = new TestActivationTokenRepository();
        userUpdateRepository = new TestUserUpdateRepository();
        transactions = new TestTransactions();

        useCase = new SetNewUserPasswordUseCase(
                new ActivationTokenConsumer(activationTokenRepository, transactions),
                userUpdateRepository,
                passwordHasher);

        activationTokenFactory = new ActivationTokenFactory(Clock.systemUTC());
    }


    @ParameterizedTest
    @MethodSource("invalidPasswordCases")
    void shouldThrowExceptionGivenInvalidPassword(String password) {
        var command = new SetNewPasswordCommand(password, TestRandom.string());

        Assertions.assertThatThrownBy(() -> useCase.handle(command))
                .isEqualTo(new InvalidPasswordException());
    }

    @ParameterizedTest
    @MethodSource("invalidActivationTokens")
    void shouldThrowExceptionGivenInvalidActivationToken(String activationToken) {
        var command = new SetNewPasswordCommand("ComplexPassword134", activationToken);

        Assertions.assertThatThrownBy(() -> useCase.handle(command))
                .isInstanceOf(InvalidActivationTokenException.class);
    }

    @Test
    void shouldSetUserPasswordAndDeleteActivationTokenInTransaction() {
        var userId = UUID.randomUUID();

        var activationToken = activationTokenFactory.passwordReset(userId);
        activationTokenRepository.save(activationToken);
        var activationTokenId = ActivationTokenId.ofPasswordReset(userId);

        var command = new SetNewPasswordCommand("ComplexPassword55", activationToken.token());
        var expectedPassword = passwordHasher.hash(command.password());

        transactions.test()
                .before(() -> {
                    Assertions.assertThat(activationTokenRepository.ofId(activationTokenId)).isPresent();
                    Assertions.assertThat(userUpdateRepository.updatedPassword).isNull();
                })
                .after(() -> {
                    Assertions.assertThat(activationTokenRepository.ofId(activationTokenId)).isEmpty();

                    Assertions.assertThat(userUpdateRepository.updatedPassword)
                            .isEqualTo(new Pair<>(userId, expectedPassword));
                })
                .execute(() -> useCase.handle(command));
    }

    static List<String> invalidActivationTokens() {
        return TestUserObjects.invalidActivationTokens();
    }

    static List<String> invalidPasswordCases() {
        return TestUserObjects.invalidPasswords();
    }
}
