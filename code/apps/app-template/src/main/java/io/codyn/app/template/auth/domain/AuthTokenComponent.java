package io.codyn.app.template.auth.domain;

import io.codyn.app.template._shared.domain.model.AuthenticatedUser;

import java.util.UUID;

public interface AuthTokenComponent {

    AuthTokens ofUser(UUID id);

    AuthTokens refresh(String refreshToken);

    AuthenticatedUser authenticate(String accessToken);
}
