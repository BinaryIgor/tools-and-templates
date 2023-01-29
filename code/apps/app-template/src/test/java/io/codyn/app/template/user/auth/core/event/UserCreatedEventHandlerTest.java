package io.codyn.app.template.user.auth.core.event;

import io.codyn.app.template._common.test.TestEmailServer;
import io.codyn.app.template.user.api.event.UserCreatedEvent;
import io.codyn.app.template.user.common.core.ActivationTokenData;
import io.codyn.app.template.user.common.core.ActivationTokenFactory;
import io.codyn.app.template.user.common.core.ActivationTokens;
import io.codyn.app.template.user.common.core.UserEmailSender;
import io.codyn.app.template.user.common.core.model.ActivationToken;
import io.codyn.app.template.user.common.core.model.ActivationTokenId;
import io.codyn.app.template.user.common.core.model.ActivationTokenType;
import io.codyn.app.template.user.common.core.model.EmailUser;
import io.codyn.app.template.user.common.test.TestActivationTokenRepository;
import io.codyn.app.template.user.common.test.TestTokenFactory;
import io.codyn.app.template.user.common.test.TestUserEmailsProvider;
import io.codyn.email.model.Email;
import io.codyn.test.TestClock;
import io.codyn.test.TestRandom;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.UUID;

public class UserCreatedEventHandlerTest {

    private UserCreatedEventHandler eventHandler;
    private TestEmailServer emailServer;
    private UserEmailSender userEmailSender;
    private TestActivationTokenRepository activationTokenRepository;
    private TestTokenFactory tokenFactory;
    private TestClock clock;

    @BeforeEach
    void setup() {
        emailServer = new TestEmailServer();
        userEmailSender = TestUserEmailsProvider.sender(emailServer);

        activationTokenRepository = new TestActivationTokenRepository();

        tokenFactory = new TestTokenFactory();
        clock = new TestClock();

        var activationTokens = new ActivationTokens(activationTokenRepository,
                new ActivationTokenFactory(tokenFactory, clock));

        eventHandler = new UserCreatedEventHandler(activationTokens, userEmailSender);
    }

    @Test
    void shouldSaveTokenAndSendEmail() {
        var testCase = prepareTestCase();

        Assertions.assertThat(activationTokenRepository.ofId(testCase.expectedTokenId)).isEmpty();
        Assertions.assertThat(emailServer.sentEmail).isNull();

        eventHandler.handle(testCase.event);

        var savedToken = activationTokenRepository.ofId(testCase.expectedTokenId).orElseThrow();

        Assertions.assertThat(savedToken)
                .isEqualTo(testCase.expectedActivationToken);

        var sentEmail = emailServer.sentEmail;

        Assertions.assertThat(sentEmail).isEqualTo(testCase.expectedEmail);

        Assertions.assertThat(sentEmail.textMessage()).contains(savedToken.token());
        Assertions.assertThat(sentEmail.htmlMessage()).contains(savedToken.token());
    }

    private TestCase prepareTestCase() {
        var userId = UUID.randomUUID();
        var event = new UserCreatedEvent(userId, "user-1", "user-1@email.com");

        var expectedTokenId = ActivationTokenId.ofNewUser(event.id());

        var token = tokenFactory.addNextToken(ActivationTokenData.withUserId(userId), TestRandom.string());
        var expectedActivationToken = new ActivationToken(userId, ActivationTokenType.NEW_USER, token,
                clock.instant().plus(Duration.ofMinutes(15)));

        userEmailSender.sendAccountActivation(new EmailUser(event.name(), event.email()), token);
        var expectedEmail = emailServer.sentEmail;
        emailServer.clear();

        return new TestCase(event, expectedTokenId, expectedActivationToken, expectedEmail);
    }

    record TestCase(UserCreatedEvent event,
                    ActivationTokenId expectedTokenId,
                    ActivationToken expectedActivationToken,
                    Email expectedEmail) {
    }
}
