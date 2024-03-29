package io.codyn.app.template.auth.api;

import io.codyn.app.template.auth.core.AuthTokens;

import java.util.UUID;

public interface AuthClient {

    AuthTokens ofUser(UUID id);

    AuthTokens refresh(String refreshToken);
}
