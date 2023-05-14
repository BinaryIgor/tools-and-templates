package io.codyn.app.template.user.auth.core.usecase;

import io.codyn.app.template._common.core.model.UserState;
import io.codyn.app.template.user.auth.test.TestUserUpdateRepository;
import io.codyn.app.template.user.common.core.ActivationTokenConsumer;
import io.codyn.app.template.user.common.core.ActivationTokenFactory;
import io.codyn.app.template.user.common.core.UserStateChangedEvent;
import io.codyn.app.template.user.common.core.exception.InvalidActivationTokenException;
import io.codyn.app.template.user.common.core.model.ActivationTokenId;
import io.codyn.app.template.user.common.test.TestActivationTokenRepository;
import io.codyn.app.template.user.common.test.TestUserObjects;
import io.codyn.test.TestTransactions;
import io.codyn.test.event.TestLocalEvents;
import io.codyn.types.Pair;
import io.codyn.types.event.InMemoryEvents;
import io.codyn.types.event.LocalEvents;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.time.Clock;
import java.util.List;
import java.util.UUID;

public class ActivateUserUseCaseTest {

    private ActivateUserUseCase useCase;
    private TestActivationTokenRepository activationTokenRepository;
    private TestUserUpdateRepository userUpdateRepository;
    private TestTransactions transactions;
    private ActivationTokenFactory activationTokenFactory;
    private LocalEvents events;

    @BeforeEach
    void setup() {
        activationTokenRepository = new TestActivationTokenRepository();
        userUpdateRepository = new TestUserUpdateRepository();
        transactions = new TestTransactions();
        events = new InMemoryEvents();

        useCase = new ActivateUserUseCase(
                new ActivationTokenConsumer(activationTokenRepository, transactions),
                userUpdateRepository, events.publisher());

        activationTokenFactory = new ActivationTokenFactory(Clock.systemUTC());
    }

    @ParameterizedTest
    @MethodSource("invalidActivationTokens")
    void shouldThrowExceptionGivenInvalidActivationToken(String activationToken) {
        Assertions.assertThatThrownBy(() -> useCase.handle(activationToken))
                .isInstanceOf(InvalidActivationTokenException.class);
    }

    @Test
    void shouldUpdateUserStatePublishEventAndDeleteActivationTokenInTransaction() {
        var userId = UUID.randomUUID();

        var activationToken = activationTokenFactory.newUser(userId);
        activationTokenRepository.save(activationToken);
        var activationTokenId = ActivationTokenId.ofNewUser(userId);

        var eventsCaptor = TestLocalEvents.subscribe(events, UserStateChangedEvent.class);

        transactions.test()
                .before(() -> {
                    Assertions.assertThat(activationTokenRepository.ofId(activationTokenId)).isPresent();
                    Assertions.assertThat(userUpdateRepository.updatedState).isNull();
                    Assertions.assertThat(eventsCaptor.last()).isNull();
                })
                .after(() -> {
                    Assertions.assertThat(activationTokenRepository.ofId(activationTokenId)).isEmpty();

                    Assertions.assertThat(userUpdateRepository.updatedState)
                            .isEqualTo(new Pair<>(userId, UserState.ACTIVATED));

                    Assertions.assertThat(eventsCaptor.last())
                            .isEqualTo(new UserStateChangedEvent(userId, UserState.ACTIVATED));
                })
                .execute(() -> useCase.handle(activationToken.token()));
    }

    static List<String> invalidActivationTokens() {
        return TestUserObjects.invalidActivationTokens();
    }
}
