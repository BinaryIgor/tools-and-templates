package io.codyn.app.template.auth.core;

import com.auth0.jwt.algorithms.Algorithm;
import io.codyn.app.template._common.core.exception.InvalidAuthTokenException;
import io.codyn.app.template._common.core.model.UserRole;
import io.codyn.app.template._common.core.model.UserRoles;
import io.codyn.app.template._common.core.model.UserState;
import io.codyn.app.template.auth.api.AuthenticatedUser;
import io.codyn.app.template.auth.api.UserAuthData;
import io.codyn.app.template.auth.test.TestUserAuthDataRepository;
import io.codyn.test.TestClock;
import io.codyn.test.TestRandom;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.time.Duration;
import java.time.Instant;
import java.util.Base64;
import java.util.UUID;
import java.util.stream.Stream;

public class JwtAuthTokensTest {

    private static final String ISSUER = "issuer-" + UUID.randomUUID();
    private static final byte[] TOKEN_KEY = TestRandom.bytes();
    private static final Duration ACCESS_TOKEN_DURATION = Duration.ofMinutes(15);
    private static final Duration REFRESH_TOKEN_DURATION = Duration.ofHours(1);
    private static final TestClock CLOCK = new TestClock();
    private JwtAuthTokens component;
    private TestUserAuthDataRepository authDataRepository;

    @BeforeEach
    void setup() {
        authDataRepository = new TestUserAuthDataRepository();

        var componentConfig = new JwtAuthTokens.Config(ISSUER, TOKEN_KEY, ACCESS_TOKEN_DURATION,
                REFRESH_TOKEN_DURATION, CLOCK);

        component = new JwtAuthTokens(authDataRepository, componentConfig);
    }

    @AfterEach
    void tearDown() {
        CLOCK.setTime(Instant.now());
    }

    @Test
    void shouldIssueTokensForUser() {
        var userId = UUID.randomUUID();

        var tokens = component.ofUser(userId);

        var expectedAccessTokenExpiresAt = CLOCK.instant().plus(ACCESS_TOKEN_DURATION);
        var expectedRefreshTokenExpiresAt = CLOCK.instant().plus(REFRESH_TOKEN_DURATION);

        Assertions.assertThat(tokens.access().expiresAt())
                .isEqualTo(expectedAccessTokenExpiresAt);
        Assertions.assertThat(tokens.refresh().expiresAt())
                .isEqualTo(expectedRefreshTokenExpiresAt);
    }

    @Test
    void shouldAuthenticateValidAccessToken() {
        var user = prepareRandomUser();

        var accessToken = component.ofUser(user.id()).access();

        Assertions.assertThat(component.authenticate(accessToken.value()))
                .isEqualTo(user);
    }

    @ParameterizedTest
    @MethodSource("invalidAccessTokens")
    void shouldThrowExceptionGivenInvalidAccessToken(String accessToken) {
        assertAuthenticateThrowsInvalidAuthTokenException(accessToken);
    }

    private void assertAuthenticateThrowsInvalidAuthTokenException(String token) {
        Assertions.assertThatThrownBy(() -> component.authenticate(token))
                .isInstanceOf(InvalidAuthTokenException.class)
                .hasMessageContaining(AuthTokenType.ACCESS.name());
    }

    @Test
    void shouldThrowExceptionGivenOutdatedAccessToken() {
        var user = prepareRandomUser();

        CLOCK.moveBack(ACCESS_TOKEN_DURATION.plusSeconds(1));

        var accessToken = component.ofUser(user.id()).access().value();

        CLOCK.setTime(Instant.now());

        assertAuthenticateThrowsInvalidAuthTokenException(accessToken);
    }

    @ParameterizedTest
    @MethodSource("invalidRefreshTokens")
    void shouldThrowExceptionGivenInvalidRefreshToken(String refreshToken) {
        assertRefreshThrowsInvalidAuthTokenException(refreshToken);
    }

    private void assertRefreshThrowsInvalidAuthTokenException(String token) {
        Assertions.assertThatThrownBy(() -> component.refresh(token))
                .isInstanceOf(InvalidAuthTokenException.class)
                .hasMessageContaining(AuthTokenType.REFRESH.name());
    }

    @Test
    void shouldThrowExceptionGivenOutdatedRefreshToken() {
        var user = prepareRandomUser();

        CLOCK.moveBack(REFRESH_TOKEN_DURATION.plusSeconds(1));

        var refreshToken = component.ofUser(user.id()).refresh().value();

        CLOCK.setTime(Instant.now());

        assertRefreshThrowsInvalidAuthTokenException(refreshToken);
    }

    @Test
    void shouldThrowExceptionWhileRefreshingWithAccessToken() {
        var user = prepareRandomUser();

        var accessToken = component.ofUser(user.id()).access().value();

        assertRefreshThrowsInvalidAuthTokenException(accessToken);
    }

    @Test
    void shouldGenerateNewTokensGivenValidRefreshToken() {
        var user = prepareRandomUser();

        var firstTokens = component.ofUser(user.id());

        CLOCK.moveForward(Duration.ofSeconds(1));

        var secondTokens = component.refresh(firstTokens.refresh().value());

        Assertions.assertThat(firstTokens).isNotEqualTo(secondTokens);

        var secondAccessToken = secondTokens.access().value();

        Assertions.assertThat(component.authenticate(secondAccessToken))
                .isEqualTo(user);
    }

    private AuthenticatedUser prepareRandomUser() {
        var id = UUID.randomUUID();
        var state = TestRandom.oneOf(UserState.values());
        var roles = UserRoles.of(TestRandom.fragment(UserRole.values()));

        authDataRepository.addUserData(new UserAuthData(id, state, roles.roles()));

        return new AuthenticatedUser(id, state, roles);
    }

    static Stream<String> invalidAccessTokens() {
        return invalidTokens(AuthTokenType.ACCESS);
    }

    static Stream<String> invalidRefreshTokens() {
        return invalidTokens(AuthTokenType.REFRESH);
    }

    private static Stream<String> invalidTokens(AuthTokenType type) {
        var invalidIssuerToken = newJwtToken("another-issuer", type, TOKEN_KEY);
        var invalidSecretToken = newJwtToken(ISSUER, type, TestRandom.bytes());
        var invalidTypeToken = newJwtToken(ISSUER,
                type == AuthTokenType.ACCESS ? AuthTokenType.REFRESH : AuthTokenType.ACCESS,
                TOKEN_KEY);

        return Stream.of(null, "",
                TestRandom.string(), Base64.getEncoder().encodeToString(TestRandom.bytes()),
                invalidIssuerToken, invalidSecretToken, invalidTypeToken);
    }

    private static String newJwtToken(String issuer, AuthTokenType type, byte[] key) {
        return JwtAuthTokens.newToken(issuer,
                UUID.randomUUID(),
                type,
                CLOCK.instant(),
                CLOCK.instant().plusSeconds(1000),
                Algorithm.HMAC512(key));
    }
}
