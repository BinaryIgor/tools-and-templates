package io.codyn.app.template.user.auth.core.usecase;

import io.codyn.app.template._common.core.exception.InvalidEmailException;
import io.codyn.app.template._common.test.EmailAssertions;
import io.codyn.app.template._common.test.TestEmailServer;
import io.codyn.app.template.user.auth.test.TestUserRepository;
import io.codyn.app.template.user.common.core.UserEmailSender;
import io.codyn.app.template.user.common.core.UserExceptions;
import io.codyn.app.template.user.common.core.model.ActivationTokenId;
import io.codyn.app.template.user.common.core.model.ActivationTokenType;
import io.codyn.app.template.user.common.core.model.EmailUser;
import io.codyn.app.template.user.common.test.TestActivationTokenRepository;
import io.codyn.app.template.user.common.test.TestUserEmailsProvider;
import io.codyn.app.template.user.common.test.TestUserObjects;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;

public class ResendUserAccountActivationTokenUseCaseTest {

    private ResendUserAccountActivationTokenUseCase useCase;
    private TestUserRepository userRepository;
    private TestActivationTokenRepository activationTokenRepository;
    private TestEmailServer emailServer;
    private UserEmailSender userEmailSender;

    @BeforeEach
    void setup() {
        userRepository = new TestUserRepository();
        activationTokenRepository = new TestActivationTokenRepository();

        emailServer = new TestEmailServer();
        userEmailSender = TestUserEmailsProvider.sender(emailServer);

        useCase = new ResendUserAccountActivationTokenUseCase(userRepository,
                activationTokenRepository, userEmailSender);
    }

    @ParameterizedTest
    @MethodSource("invalidEmailCases")
    void shouldThrowExceptionGivenInvalidEmail(String email) {
        Assertions.assertThatThrownBy(() -> useCase.handle(email))
                .isEqualTo(new InvalidEmailException(email));
    }

    @Test
    void shouldThrowExceptionGivenEmailOfNonExistingUser() {
        var someEmail = "email1@gmail.com";

        Assertions.assertThatThrownBy(() -> useCase.handle(someEmail))
                .isEqualTo(UserExceptions.userOfEmailNotFound(someEmail));
    }

    @Test
    void shouldThrowExceptionGivenEmailOfUserWithoutValidActivationToken() {
        var user = TestUserObjects.user();
        userRepository.create(user);

        activationTokenRepository.save(TestUserObjects.activationToken(user.id(),
                ActivationTokenType.EMAIL_CHANGE));

        Assertions.assertThatThrownBy(() -> useCase.handle(user.email()))
                .isEqualTo(UserExceptions.activationTokenNotFound(ActivationTokenId.ofNewUser(user.id())));
    }

    @Test
    void shouldSendEmailWithPreviouslyGeneratedActivationToken() {
        var user = TestUserObjects.user();
        userRepository.create(user);

        var activationToken = TestUserObjects.activationToken(user.id(), ActivationTokenType.NEW_USER);
        activationTokenRepository.save(activationToken);

        var expectedEmail = emailServer.sendAndCaptureExpectedEmail(() -> {
            userEmailSender.sendAccountActivation(new EmailUser(user.id(), user.name(), user.email()),
                    activationToken.token());
        });

        Assertions.assertThat(emailServer.sentEmail).isNull();

        useCase.handle(user.email());

        var sentEmail = emailServer.sentEmail;

        Assertions.assertThat(sentEmail).isEqualTo(expectedEmail);
        EmailAssertions.messageContains(sentEmail, activationToken.token());
    }

    static List<String> invalidEmailCases() {
        return TestUserObjects.invalidEmails();
    }
}
