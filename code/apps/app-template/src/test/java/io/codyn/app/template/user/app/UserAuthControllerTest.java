package io.codyn.app.template.user.app;

import io.codyn.app.template.SpringIntegrationTest;
import io.codyn.app.template._common.app.exception.ApiExceptionResponse;
import io.codyn.app.template._common.core.model.UserState;
import io.codyn.app.template.auth.core.AuthTokens;
import io.codyn.app.template.user.auth.app.model.ActivationToken;
import io.codyn.app.template.user.auth.app.model.ApiNewUserRequest;
import io.codyn.app.template.user.auth.app.model.RefreshToken;
import io.codyn.app.template.user.auth.core.model.*;
import io.codyn.app.template.user.auth.core.repository.UserRepository;
import io.codyn.app.template.user.common.core.ActivationTokenRepository;
import io.codyn.app.template.user.common.core.model.ActivationTokenId;
import io.codyn.test.http.TestHttpClient;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.UUID;

public class UserAuthControllerTest extends SpringIntegrationTest {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ActivationTokenRepository tokenRepository;

    @Test
    void shouldAllowToSignUpAndThenSignInUsingNewAccount() {
        shouldAllowToSignUpAndThenSignIn();
    }

    private SignedInUser shouldAllowToSignUpAndThenSignIn() {
        var user = new ApiNewUserRequest("some-user", "some-email@gmail.com",
                "Some-complex-password12");
        shouldSignUp(user);
        var userId = shouldActivateUser(user.email());
        return shouldSignInReturningCurrentUserDataAndTokens(userId, user.email(), user.name(), user.password());
    }

    @Test
    void shouldReturnExceptionResponseWithInvalidSignUp() {
        expectBadRequestExceptionResponse(
                testHttpClient.test()
                        .path(userAuthPath("sign-up"))
                        .POST()
                        .body(new ApiNewUserRequest(null, null, null)));
    }

    private void expectBadRequestExceptionResponse(TestHttpClient.TestBuilder builder) {
        builder.expectedBadRequestStatus()
                .executeReturningObject(ApiExceptionResponse.class);
    }

    @Test
    void shouldReturnExceptionResponseWithInvalidSignIn() {
        expectBadRequestExceptionResponse(
                testHttpClient.test()
                        .path(userAuthPath("sign-in"))
                        .POST()
                        .body(new UserSignInRequest("some-email@", "pass13")));
    }

    @Test
    void shouldReturnExceptionResponseWithInvalidAccountActivation() {
        expectBadRequestExceptionResponse(
                testHttpClient.test()
                        .path(userAuthPath("activate-account"))
                        .POST()
                        .body(new ActivationToken("some")));
    }

    @Test
    void shouldAllowToResetPassword() {
        var user = shouldAllowToSignUpAndThenSignIn().data();

        var token = shouldSendResetPasswordToken(user.id(), user.email());

        var newPassword = "SomeComplicatedPassword";
        shouldSetNewPasswordUsingToken(newPassword, token);

        shouldSignInReturningCurrentUserDataAndTokens(user.id(), user.email(), user.name(), newPassword);
    }

    @Test
    void shouldReturnExceptionResponseWithInvalidNewPassword() {
        expectBadRequestExceptionResponse(
                testHttpClient.test()
                        .path(userAuthPath("set-new-password"))
                        .POST()
                        .body(new NewPasswordRequest("pass33", null)));
    }

    @Test
    void shouldReturnExceptionResponseWithInvalidPasswordReset() {
        expectBadRequestExceptionResponse(
                testHttpClient.test()
                        .path(userAuthPath("reset-password/do@"))
                        .POST());
    }

    @Test
    void shouldAllowToRefreshTokens() {
        var tokens = shouldAllowToSignUpAndThenSignIn().tokens();

        var refreshedTokens = testHttpClient.test()
                .path(userAuthPath("refresh-tokens"))
                .POST()
                .body(new RefreshToken(tokens.refresh().value()))
                .executeReturningObject(AuthTokens.class);

        Assertions.assertThat(refreshedTokens).isNotEqualTo(tokens);
    }

    @Test
    void shouldReturnExceptionResponseWithInvalidRefreshToken() {
        testHttpClient.test()
                .path(userAuthPath("refresh-tokens"))
                .POST()
                .body(new RefreshToken("invalid-token"))
                .expectedStatus(401)
                .executeReturningObject(ApiExceptionResponse.class);
    }

    private void shouldSignUp(ApiNewUserRequest request) {
        testHttpClient.test()
                .path(userAuthPath("sign-up"))
                .POST()
                .body(request)
                .expectedStatus(201)
                .execute();
    }

    private String userAuthPath(String path) {
        return "/user-auth/" + path;
    }

    private UUID shouldActivateUser(String email) {
        var userId = userRepository.ofEmail(email).orElseThrow().id();
        var activationToken = tokenRepository.ofId(ActivationTokenId.ofNewUser(userId)).orElseThrow().token();

        lastSentEmailContains(activationToken);

        testHttpClient.test()
                .path(userAuthPath("activate-account"))
                .POST()
                .body(new ActivationToken(activationToken))
                .execute();

        return userId;
    }

    private void lastSentEmailContains(String string) {
        Assertions.assertThat(emailServer.sentEmail.htmlMessage()).contains(string);
        Assertions.assertThat(emailServer.sentEmail.textMessage()).contains(string);
    }

    private SignedInUser shouldSignInReturningCurrentUserDataAndTokens(UUID id, String email, String name,
                                                                       String password) {
        var response = testHttpClient.test()
                .path(userAuthPath("sign-in"))
                .POST()
                .body(new UserSignInRequest(email, password))
                .executeReturningObject(SignedInUserStep.class);

        var expectedCurrentData = new CurrentUserData(id, email, name, UserState.ACTIVATED, List.of());

        Assertions.assertThat(response.secondFactor()).isFalse();
        Assertions.assertThat(response.user().data()).isEqualTo(expectedCurrentData);
        Assertions.assertThat(response.user().tokens()).isNotNull();

        return response.user();
    }

    private String shouldSendResetPasswordToken(UUID id, String email) {
        testHttpClient.test()
                .path(userAuthPath("reset-password/" + email))
                .POST()
                .execute();

        var token = tokenRepository.ofId(ActivationTokenId.ofPasswordReset(id)).orElseThrow().token();

        lastSentEmailContains(token);

        return token;
    }

    private void shouldSetNewPasswordUsingToken(String password, String token) {
        testHttpClient.test()
                .path(userAuthPath("set-new-password"))
                .POST()
                .body(new NewPasswordRequest(password, token))
                .execute();
    }
}
