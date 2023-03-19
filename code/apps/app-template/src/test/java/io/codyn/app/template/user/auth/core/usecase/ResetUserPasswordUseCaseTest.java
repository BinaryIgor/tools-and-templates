package io.codyn.app.template.user.auth.core.usecase;

import io.codyn.app.template._common.core.email.Emails;
import io.codyn.app.template._common.core.exception.InvalidEmailException;
import io.codyn.app.template._common.test.EmailAssertions;
import io.codyn.app.template._common.test.TestEmailServer;
import io.codyn.app.template.user.auth.test.TestUserRepository;
import io.codyn.app.template.user.common.core.ActivationTokenData;
import io.codyn.app.template.user.common.core.ActivationTokenFactory;
import io.codyn.app.template.user.common.core.ActivationTokens;
import io.codyn.app.template.user.common.core.UserExceptions;
import io.codyn.app.template.user.common.core.model.ActivationTokenId;
import io.codyn.app.template.user.common.test.TestActivationTokenRepository;
import io.codyn.app.template.user.common.test.TestTokenFactory;
import io.codyn.app.template.user.common.test.TestUserEmailsProvider;
import io.codyn.app.template.user.common.test.TestUserObjects;
import io.codyn.test.TestClock;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;

public class ResetUserPasswordUseCaseTest {

    private ResetUserPasswordUseCase useCase;
    private TestUserRepository userRepository;
    private TestEmailServer emailServer;
    private TestActivationTokenRepository activationTokenRepository;
    private TestTokenFactory tokenFactory;
    private ActivationTokenFactory activationTokenFactory;

    @BeforeEach
    void setup() {
        userRepository = new TestUserRepository();

        emailServer = new TestEmailServer();

        activationTokenRepository = new TestActivationTokenRepository();

        tokenFactory = new TestTokenFactory(new TestClock());
        activationTokenFactory = tokenFactory.activationTokenFactory();

        useCase = new ResetUserPasswordUseCase(userRepository,
                new ActivationTokens(activationTokenRepository, activationTokenFactory),
                TestUserEmailsProvider.sender(emailServer));
    }

    @ParameterizedTest
    @MethodSource("invalidEmailCases")
    void shouldThrowExceptionGivenInvalidEmail(String email) {
        Assertions.assertThatThrownBy(() -> useCase.handle(email))
                .isEqualTo(new InvalidEmailException(email));
    }

    @Test
    void shouldThrowExceptionGivenNonExistingUser() {
        var email = "email@gmail.com";

        Assertions.assertThatThrownBy(() -> useCase.handle(email))
                .isEqualTo(UserExceptions.userOfEmailNotFound(email));
    }

    @Test
    void shouldSaveActivationTokenAndSendItViaEmail() {
        //given
        var user = TestUserObjects.user();
        var activationTokenId = ActivationTokenId.ofPasswordReset(user.id());

        tokenFactory.addNextToken(ActivationTokenData.withUserId(user.id()));
        var expectedActivationToken = activationTokenFactory.passwordReset(user.id());

        userRepository.addUser(user);

        Assertions.assertThat(activationTokenRepository.ofId(activationTokenId)).isEmpty();
        Assertions.assertThat(emailServer.sentEmail).isNull();

        //when
        useCase.handle(user.email());

        //then
        Assertions.assertThat(activationTokenRepository.ofId(activationTokenId))
                .get()
                .isEqualTo(expectedActivationToken);

        EmailAssertions.meetsExpectations(emailServer.sentEmail,
                EmailAssertions.expectations()
                        .messageContains(user.name(), expectedActivationToken.token())
                        .tagIsEqual(Emails.Types.PASSWORD_RESET));
    }

    static List<String> invalidEmailCases() {
        return TestUserObjects.invalidEmails();
    }
}
