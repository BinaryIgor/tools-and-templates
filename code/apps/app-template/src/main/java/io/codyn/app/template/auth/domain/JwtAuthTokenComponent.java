package io.codyn.app.template.auth.domain;

import io.codyn.app.template._shared.domain.model.AuthenticatedUser;

import java.util.UUID;

public class JwtAuthTokenComponent implements AuthTokenComponent {

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
}
