package io.codyn.app.template.auth.domain;

import io.codyn.app.template._shared.domain.exception.InvalidAuthTokenException;
import io.codyn.app.template._shared.domain.exception.ResourceNotFoundException;
import io.codyn.app.template._shared.domain.model.AuthenticatedUser;
import io.codyn.app.template._shared.domain.model.UserRoles;
import io.codyn.app.template._shared.domain.model.UserState;
import org.springframework.stereotype.Component;

import java.time.Clock;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Component
public class FakeAuthTokenComponent implements AuthTokenComponent {

    private static final long ACCESS_TOKEN_EXPIRATION = TimeUnit.HOURS.toSeconds(1);
    private static final long REFRESH_TOKEN_EXPIRATION = TimeUnit.DAYS.toSeconds(1);
    private final Map<UUID, AuthenticatedUser> users = new HashMap<>();
    private final Clock clock;

    public FakeAuthTokenComponent(Clock clock) {
        this.clock = clock;

        List.of(new AuthenticatedUser(UUID.randomUUID(), UserState.CREATED, UserRoles.empty()),
                        new AuthenticatedUser(UUID.randomUUID(), UserState.ACTIVATED, UserRoles.empty()))
                .forEach(a -> users.put(a.id(), a));
    }

    @Override
    public AuthTokens ofUser(UUID id) {
        var now = clock.instant();
        return new AuthTokens(
                new AuthToken(id + "-access", now.plusSeconds(ACCESS_TOKEN_EXPIRATION)),
                new AuthToken(id + "-refresh", now.plusSeconds(REFRESH_TOKEN_EXPIRATION)));
    }

    @Override
    public AuthTokens refresh(String refreshToken) {
        try {
            var userId = userIdFromToken(refreshToken);
            return ofUser(userId);
        } catch (Exception e) {
            throw InvalidAuthTokenException.invalidRefreshToken();
        }
    }

    private UUID userIdFromToken(String token) {
        var userId = token.split("-")[0];
        return UUID.fromString(userId);
    }

    @Override
    public AuthenticatedUser authenticate(String accessToken) {
        UUID userId;
        try {
            userId = userIdFromToken(accessToken);
        } catch (Exception e) {
            throw InvalidAuthTokenException.invalidAccessToken();
        }

        UUID finalUserId = userId;
        return Optional.ofNullable(users.get(userId))
                .orElseThrow(() ->
                        new ResourceNotFoundException("User of %s id doesn't exist".formatted(finalUserId)));
    }
}
