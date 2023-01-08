package io.codyn.app.template.auth.api;

import io.codyn.app.template.auth.domain.AuthTokens;

import java.util.UUID;

public interface AuthClient {

    AuthTokens ofUser(UUID id);

    AuthTokens refresh(String refreshToken);

    AuthenticatedUser authenticate(String accessToken);

    AuthenticatedUser currentUser();
}
