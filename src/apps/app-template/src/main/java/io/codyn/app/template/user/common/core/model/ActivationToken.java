package io.codyn.app.template.user.common.core.model;

import io.codyn.app.template._common.core.model.ActivationTokenType;

import java.time.Instant;
import java.util.UUID;

public record ActivationToken(UUID userId,
                              ActivationTokenType type,
                              ActivationTokenStatus status,
                              String token,
                              Instant expiresAt) {

    public static ActivationToken ofInitialStatus(UUID userId,
                                                  ActivationTokenType type,
                                                  String token,
                                                  Instant expiresAt) {
        return new ActivationToken(userId, type, ActivationTokenStatus.SENDING, token, expiresAt);
    }

    public ActivationToken withToken(String token) {
        return new ActivationToken(userId, type, status, token, expiresAt);
    }

    public ActivationToken withExpiresAt(Instant expiresAt) {
        return new ActivationToken(userId, type, status, token, expiresAt);
    }
}
