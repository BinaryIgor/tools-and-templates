package io.codyn.app.template.user.auth.core.usecase;

import io.codyn.app.template._common.core.email.Emails;
import io.codyn.app.template._common.core.exception.InvalidEmailException;
import io.codyn.app.template._common.test.EmailAssertions;
import io.codyn.app.template._common.test.TestEmailServer;
import io.codyn.app.template.user.auth.test.TestUserRepository;
import io.codyn.app.template.user.common.core.UserExceptions;
import io.codyn.app.template.user.common.core.model.ActivationTokenId;
import io.codyn.app.template._common.core.model.ActivationTokenType;
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

    @BeforeEach
    void setup() {
        userRepository = new TestUserRepository();
        activationTokenRepository = new TestActivationTokenRepository();

        emailServer = new TestEmailServer();

        useCase = new ResendUserAccountActivationTokenUseCase(userRepository,
                activationTokenRepository, TestUserEmailsProvider.sender(emailServer));
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

        var emailExpectations = EmailAssertions.expectations()
                .sentTo(user.name(), user.email())
                .messageContains(user.name(), activationToken.token())
                .tagIsEqual(Emails.Types.USER_ACTIVATION)
                .hasMetadata(Emails.Metadata.ofActivationToken(user.id(), ActivationTokenType.NEW_USER));

        useCase.handle(user.email());

        EmailAssertions.meetsExpectations(emailServer.sentEmail, emailExpectations);
    }

    static List<String> invalidEmailCases() {
        return TestUserObjects.invalidEmails();
    }
}
