package io.codyn.app.template.auth.domain;

import io.codyn.app.template._shared.domain.model.AuthenticatedUser;
import io.codyn.app.template.user.api.UserAuthClient;

import java.util.UUID;

public class JwtAuthTokenComponent implements AuthTokenComponent {

    private final UserAuthClient authClient;
    private final byte[] tokenKey;
    private final long accessTokenDuration;
    private final long refreshTokenDuration;

    public JwtAuthTokenComponent(UserAuthClient authClient, Config config) {
        this.authClient = authClient;
        this.tokenKey = config.tokenKey;
        this.accessTokenDuration = config.accessTokenDuration;
        this.refreshTokenDuration = config.refreshTokenDuration;
    }

    @Override
    public AuthTokens ofUser(UUID id) {
        return null;
    }

    @Override
    public AuthTokens refresh(String refreshToken) {
        return null;
    }

    @Override
    public AuthenticatedUser authenticate(String accessToken) {
        return null;
    }

    public record Config(
            byte[] tokenKey,
            long accessTokenDuration,
            long refreshTokenDuration) {
    }
}
