package io.codyn.app.template.user.account.core;

import io.codyn.app.template._common.core.exception.EmailNotReachableException;
import io.codyn.app.template._common.core.exception.InvalidEmailException;
import io.codyn.app.template._common.test.EmailAssertions;
import io.codyn.app.template._common.test.TestEmailServer;
import io.codyn.app.template.user.account.core.model.ChangeUserEmailCommand;
import io.codyn.app.template.user.account.core.usecase.ChangeUserEmailUseCase;
import io.codyn.app.template.user.auth.test.TestUserRepository;
import io.codyn.app.template.user.common.core.ActivationTokenData;
import io.codyn.app.template.user.common.core.ActivationTokenFactory;
import io.codyn.app.template.user.common.core.ActivationTokens;
import io.codyn.app.template.user.common.core.model.ActivationToken;
import io.codyn.app.template.user.common.core.model.ActivationTokenId;
import io.codyn.app.template.user.common.core.model.ActivationTokenType;
import io.codyn.app.template.user.common.test.TestActivationTokenRepository;
import io.codyn.app.template.user.common.test.TestTokenFactory;
import io.codyn.app.template.user.common.test.TestUserEmailsProvider;
import io.codyn.app.template.user.common.test.TestUserObjects;
import io.codyn.email.model.EmailAddress;
import io.codyn.test.TestClock;
import io.codyn.test.TestRandom;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.time.Duration;
import java.util.List;
import java.util.UUID;

public class ChangeUserEmailUseCaseTest {

    private ChangeUserEmailUseCase useCase;
    private TestUserRepository userRepository;
    private TestEmailServer emailServer;
    private TestActivationTokenRepository activationTokenRepository;
    private TestTokenFactory tokenFactory;
    private TestClock clock;


    @BeforeEach
    void setup() {
        userRepository = new TestUserRepository();
        emailServer = new TestEmailServer();
        activationTokenRepository = new TestActivationTokenRepository();
        tokenFactory = new TestTokenFactory();
        clock = new TestClock();

        var activationTokens = new ActivationTokens(activationTokenRepository,
                new ActivationTokenFactory(tokenFactory, clock));

        useCase = new ChangeUserEmailUseCase(userRepository, activationTokens,
                TestUserEmailsProvider.sender(emailServer));
    }

    @ParameterizedTest
    @MethodSource("invalidEmailCases")
    void shouldThrowExceptionGivenInvalidEmail(String email) {
        var command = new ChangeUserEmailCommand(UUID.randomUUID(), email);

        Assertions.assertThatThrownBy(() -> useCase.handle(command))
                .isEqualTo(new InvalidEmailException(email));
    }

    @Test
    void shouldThrowExceptionGivenUnreachableEmail() {
        var unreachableEmail = "some@email-%s.com".formatted(TestRandom.string());
        var command = new ChangeUserEmailCommand(UUID.randomUUID(), unreachableEmail);

        Assertions.assertThatThrownBy(() -> useCase.handle(command))
                .isEqualTo(new EmailNotReachableException(unreachableEmail));
    }

    @Test
    void shouldSendEmailWithChangeConfirmationToken() {
        var user = TestUserObjects.user();
        var userId = userRepository.create(user);
        var command = new ChangeUserEmailCommand(userId, "new-email@gmail.com");

        var tokenId = ActivationTokenId.ofEmailChange(userId);
        var token = tokenFactory.addNextToken(ActivationTokenData.withUserIdAndNewEmail(userId, command.email()),
                TestRandom.string());
        var expectedActivationToken = new ActivationToken(userId, ActivationTokenType.EMAIL_CHANGE, token,
                clock.instant().plus(Duration.ofHours(1)));

        useCase.handle(command);

        var savedToken = activationTokenRepository.ofId(tokenId).orElseThrow();

        Assertions.assertThat(savedToken)
                .isEqualTo(expectedActivationToken);

        assertEmailWithChangeConfirmationTokenWasSent(user.name(), command.email(),
                user.email(), savedToken.token());
    }

    private void assertEmailWithChangeConfirmationTokenWasSent(String name,
                                                               String newEmail,
                                                               String oldEmail,
                                                               String savedToken) {
        var sentEmail = emailServer.sentEmail;

        EmailAssertions.toIsEqualTo(sentEmail, EmailAddress.ofNameEmail(name, newEmail));
        EmailAssertions.messageContains(sentEmail, name, oldEmail, savedToken);
    }

    static List<String> invalidEmailCases() {
        return TestUserObjects.invalidEmails();
    }

}
