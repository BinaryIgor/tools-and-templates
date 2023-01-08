package io.codyn.app.template.auth.domain;

import io.codyn.app.template._shared.domain.model.UserRole;
import io.codyn.app.template._shared.domain.model.UserRoles;
import io.codyn.app.template._shared.domain.model.UserState;
import io.codyn.app.template.auth.api.AuthenticatedUser;
import io.codyn.app.template.auth.api.UserAuthData;
import io.codyn.app.template.auth.test.TestUserAuthDataRepository;
import io.codyn.commons.test.TestClock;
import io.codyn.commons.test.TestRandom;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.UUID;

public class JwtAuthTokenComponentTest {

    private static final String ISSUER = "issuer-" + UUID.randomUUID();
    private static final byte[] TOKEN_KEY = TestRandom.bytes();
    private static final Duration ACCESS_TOKEN_DURATION = Duration.ofMinutes(15);
    private static final Duration REFRESH_TOKEN_DURATION = Duration.ofHours(1);
    private JwtAuthTokenComponent component;
    private TestUserAuthDataRepository authDataRepository;
    private TestClock clock;

    @BeforeEach
    void setup() {
        clock = new TestClock();

        authDataRepository = new TestUserAuthDataRepository();

        var componentConfig = new JwtAuthTokenComponent.Config(ISSUER, TOKEN_KEY, ACCESS_TOKEN_DURATION,
                REFRESH_TOKEN_DURATION, clock);

        component = new JwtAuthTokenComponent(authDataRepository, componentConfig);
    }

    @Test
    void shouldIssueTokensForUser() {
        var userId = UUID.randomUUID();

        var tokens = component.ofUser(userId);

        var expectedAccessTokenExpiresAt = clock.instant().plus(ACCESS_TOKEN_DURATION);
        var expectedRefreshTokenExpiresAt = clock.instant().plus(REFRESH_TOKEN_DURATION);

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

    @Test
    void shouldGenerateNewTokensGivenValidRefreshToken() {
        var user = prepareRandomUser();

        var firstTokens = component.ofUser(user.id());

        clock.moveBack(Duration.ofSeconds(1));

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
}
