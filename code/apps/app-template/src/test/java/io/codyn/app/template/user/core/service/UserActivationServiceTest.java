package io.codyn.app.template.user.core.service;

import io.codyn.app.template._shared.core.model.UserState;
import io.codyn.app.template.user.core.component.ActivationTokenConsumer;
import io.codyn.app.template.user.core.component.ActivationTokenFactory;
import io.codyn.app.template.user.core.exception.InvalidActivationTokenException;
import io.codyn.app.template.user.core.model.activation.ActivationTokenId;
import io.codyn.app.template.user.test.repository.TestActivationTokenRepository;
import io.codyn.app.template.user.test.repository.TestUserUpdateRepository;
import io.codyn.test.TestRandom;
import io.codyn.test.TestTransactions;
import io.codyn.types.Pair;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.time.Clock;
import java.util.UUID;
import java.util.stream.Stream;

public class UserActivationServiceTest {

    private UserActivationService service;
    private TestActivationTokenRepository activationTokenRepository;
    private TestUserUpdateRepository userUpdateRepository;
    private TestTransactions transactions;
    private ActivationTokenFactory activationTokenFactory;

    @BeforeEach
    void setup() {
        activationTokenRepository = new TestActivationTokenRepository();
        userUpdateRepository = new TestUserUpdateRepository();
        transactions = new TestTransactions();

        service = new UserActivationService(
                new ActivationTokenConsumer(activationTokenRepository, transactions),
                userUpdateRepository);

        activationTokenFactory = new ActivationTokenFactory(Clock.systemUTC());
    }

    @ParameterizedTest
    @MethodSource("invalidActivationTokens")
    void shouldThrowExceptionGivenInvalidActivationToken(String activationToken) {
        Assertions.assertThatThrownBy(() -> service.activate(activationToken))
                .isInstanceOf(InvalidActivationTokenException.class);
    }

    @Test
    void shouldUpdateUserStateAndDeleteActivationTokenInTransaction() {
        var userId = UUID.randomUUID();

        var activationToken = activationTokenFactory.newUser(userId);
        activationTokenRepository.save(activationToken);
        var activationTokenId = ActivationTokenId.ofNewUser(userId);

        transactions.test()
                .before(() -> {
                    Assertions.assertThat(activationTokenRepository.ofId(activationTokenId)).isPresent();
                    Assertions.assertThat(userUpdateRepository.updatedState).isNull();
                })
                .after(() -> {
                    Assertions.assertThat(activationTokenRepository.ofId(activationTokenId)).isEmpty();
                    Assertions.assertThat(userUpdateRepository.updatedState)
                            .isEqualTo(new Pair<>(userId, UserState.ACTIVATED));
                })
                .execute(() -> service.activate(activationToken.token()));
    }

    static Stream<String> invalidActivationTokens() {
        return Stream.of(" ", null, TestRandom.string());
    }
}
