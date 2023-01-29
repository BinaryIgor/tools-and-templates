package io.codyn.app.template.auth.core;

import java.util.UUID;

public interface AuthTokenCreator {

    AuthTokens ofUser(UUID id);

    AuthTokens refresh(String refreshToken);
}
