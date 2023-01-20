package io.codyn.app.template.user.test;

import io.codyn.app.template.auth.api.AuthClient;
import io.codyn.app.template.auth.api.AuthenticatedUser;
import io.codyn.app.template.auth.core.AuthToken;
import io.codyn.app.template.auth.core.AuthTokenType;
import io.codyn.app.template.auth.core.AuthTokens;
import io.codyn.test.TestClock;

import java.time.Clock;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class TestAuthClient implements AuthClient {

    private final Map<String, AuthenticatedUser> tokensUsers = new HashMap<>();
    private final Clock clock;
    public AuthenticatedUser currentUser;

    public TestAuthClient(Clock clock) {
        this.clock = clock;
    }

    public TestAuthClient() {
        this(new TestClock());
    }

    public void addUser(String authToken, AuthenticatedUser user) {
        tokensUsers.put(authToken, user);
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
    public AuthenticatedUser authenticate(String accessToken) {
        return tokensUsers.get(accessToken);
    }

    @Override
    public AuthenticatedUser currentUser() {
        return currentUser;
    }
}
