package io.codyn.app.template.user.domain.model.activation;

import java.time.Instant;
import java.util.UUID;

public record ActivationToken(UUID userId,
                              ActivationTokenType type,
                              String linkId,
                              String token,
                              Instant expiresAt) {

    public ActivationToken(UUID userId,
                           ActivationTokenType type,
                           String token,
                           Instant expiresAt) {
        this(userId, type, ActivationTokenId.CONSTANT_LINK_ID, token, expiresAt);
    }

    public ActivationToken withToken(String token) {
        return new ActivationToken(userId, type, linkId, token, expiresAt);
    }

    public ActivationToken withExpiresAt(Instant expiresAt) {
        return new ActivationToken(userId, type, linkId, token, expiresAt);
    }
}
