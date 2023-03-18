package io.codyn.app.template.user.common.core.model;

import java.time.Instant;
import java.util.UUID;

//TODO status!
public record ActivationToken(UUID userId,
                              ActivationTokenType type,
                              String token,
                              Instant expiresAt) {

    public ActivationToken withToken(String token) {
        return new ActivationToken(userId, type, token, expiresAt);
    }

    public ActivationToken withExpiresAt(Instant expiresAt) {
        return new ActivationToken(userId, type, token, expiresAt);
    }
}
