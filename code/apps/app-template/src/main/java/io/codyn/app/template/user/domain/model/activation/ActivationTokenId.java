package io.codyn.app.template.user.domain.model.activation;

import java.util.UUID;

public record ActivationTokenId(UUID userId, ActivationTokenType tokenType, String linkId) {

    public static final String CONSTANT_LINK_ID = "0";

    public static ActivationTokenId ofNewEmail(UUID userId) {
        return new ActivationTokenId(userId, ActivationTokenType.NEW_EMAIL, CONSTANT_LINK_ID);
    }

    public static ActivationTokenId ofNewUser(UUID userId) {
        return new ActivationTokenId(userId, ActivationTokenType.NEW_USER, CONSTANT_LINK_ID);
    }

    public static ActivationTokenId ofPasswordReset(UUID userId) {
        return new ActivationTokenId(userId, ActivationTokenType.PASSWORD_RESET, CONSTANT_LINK_ID);
    }

    public static ActivationTokenId of(UUID userId, ActivationTokenType type, String linkId) {
        return new ActivationTokenId(userId, type, linkId);
    }
}
