package io.codyn.app.template.user.account.core;

import io.codyn.app.template.user.account.core.model.ConfirmUserEmailChangeCommand;
import io.codyn.app.template.user.account.core.usecase.ConfirmUserEmailChangeUseCase;
import io.codyn.app.template.user.auth.test.TestUserUpdateRepository;
import io.codyn.app.template.user.common.core.ActivationTokenConsumer;
import io.codyn.app.template.user.common.core.ActivationTokenFactory;
import io.codyn.app.template.user.common.core.exception.InvalidActivationTokenException;
import io.codyn.app.template.user.common.core.model.ActivationTokenId;
import io.codyn.app.template.user.common.test.TestActivationTokenRepository;
import io.codyn.app.template.user.common.test.TestUserObjects;
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

public class ConfirmUserEmailChangeUseCaseTest {

    private ConfirmUserEmailChangeUseCase useCase;
    private TestActivationTokenRepository activationTokenRepository;
    private TestUserUpdateRepository userUpdateRepository;
    private TestTransactions transactions;
    private ActivationTokenFactory activationTokenFactory;

    @BeforeEach
    void setup() {
        activationTokenRepository = new TestActivationTokenRepository();
        userUpdateRepository = new TestUserUpdateRepository();
        transactions = new TestTransactions();

        useCase = new ConfirmUserEmailChangeUseCase(
                new ActivationTokenConsumer(activationTokenRepository, transactions),
                userUpdateRepository);

        activationTokenFactory = new ActivationTokenFactory(Clock.systemUTC());
    }

    @ParameterizedTest
    @MethodSource("invalidActivationTokens")
    void shouldThrowExceptionGivenInvalidActivationToken(String activationToken) {
        var command = new ConfirmUserEmailChangeCommand(UUID.randomUUID(), activationToken);

        Assertions.assertThatThrownBy(() -> useCase.handle(command))
                .isInstanceOf(InvalidActivationTokenException.class);
    }

    @Test
    void shouldUpdateUserEmailAndDeleteActivationTokenInTransaction() {
        var userId = UUID.randomUUID();
        var newEmail = "some-new-email@email.com";

        var activationToken = activationTokenFactory.newEmail(userId, newEmail);
        activationTokenRepository.save(activationToken);
        var activationTokenId = ActivationTokenId.ofEmailChange(userId);

        var command = new ConfirmUserEmailChangeCommand(userId, activationToken.token());

        transactions.test()
                .before(() -> {
                    Assertions.assertThat(activationTokenRepository.ofId(activationTokenId)).isPresent();
                    Assertions.assertThat(userUpdateRepository.updatedEmail).isNull();
                })
                .after(() -> {
                    Assertions.assertThat(activationTokenRepository.ofId(activationTokenId)).isEmpty();
                    Assertions.assertThat(userUpdateRepository.updatedEmail)
                            .isEqualTo(new Pair<>(userId, newEmail));
                })
                .execute(() -> useCase.handle(command));
    }

    static List<String> invalidActivationTokens() {
        return TestUserObjects.invalidActivationTokens();
    }
}
