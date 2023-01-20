package io.codyn.app.template.user.core.model.activation;

import java.time.Instant;
import java.util.UUID;

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
