package io.codyn.app.template.user.common.test;

import io.codyn.app.template.auth.api.AuthClient;
import io.codyn.app.template.auth.api.AuthUserClient;
import io.codyn.app.template.auth.api.AuthenticatedUser;
import io.codyn.app.template.auth.core.AuthToken;
import io.codyn.app.template.auth.core.AuthTokenType;
import io.codyn.app.template.auth.core.AuthTokens;
import io.codyn.test.TestClock;

import java.time.Clock;
import java.util.UUID;

public class TestAuthClient implements AuthClient, AuthUserClient {

    private final Clock clock;
    public AuthenticatedUser currentUser;

    public TestAuthClient(Clock clock) {
        this.clock = clock;
    }

    public TestAuthClient() {
        this(new TestClock());
    }

    @Override
    public AuthTokens ofUser(UUID id) {
        return new AuthTokens(tokenOfUser(id, AuthTokenType.ACCESS),
                tokenOfUser(id, AuthTokenType.REFRESH));
    }

    private AuthToken tokenOfUser(UUID userId, AuthTokenType type) {
        var now = clock.instant();
        return new AuthToken(userId + "-" + type,
                type == AuthTokenType.ACCESS ? now.plusSeconds(10) : now.plusSeconds(1000));
    }

    @Override
    public AuthTokens refresh(String refreshToken) {
        var userId = UUID.fromString(refreshToken.split("-")[0]);
        return ofUser(userId);
    }

    @Override
    public AuthenticatedUser current() {
        return currentUser;
    }

    @Override
    public UUID currentId() {
        return current().id();
    }
}
