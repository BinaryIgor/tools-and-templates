package io.codyn.app.template.user.domain.model.activation;

import java.time.LocalDateTime;

public record ActivationToken(String userId,
                              ActivationTokenType type,
                              String linkId,
                              String token,
                              LocalDateTime expiresAt) {

    public ActivationToken(String userId,
                           ActivationTokenType type,
                           String token,
                           LocalDateTime expiresAt) {
        this(userId, type, ActivationTokenId.CONSTANT_LINK_ID, token, expiresAt);
    }

    public ActivationToken withToken(String token) {
        return new ActivationToken(userId, type, linkId, token, expiresAt);
    }

    public ActivationToken withExpiresAt(LocalDateTime expiresAt) {
        return new ActivationToken(userId, type, linkId, token, expiresAt);
    }
}
