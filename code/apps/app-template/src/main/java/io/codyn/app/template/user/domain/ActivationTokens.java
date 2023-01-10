package io.codyn.app.template.user.domain;

import io.codyn.app.template.user.domain.model.activation.ActivationToken;
import io.codyn.app.template.user.domain.model.activation.ActivationTokenType;

import java.time.Duration;

public class ActivationTokens {

    public static ActivationToken newUser(String userId) {
        var request = Request.builder()
                .userId(userId)
                .type(ActivationTokenType.NEW_USER)
                .validity(Duration.ofMinutes(15))
                .build();

        return idToken(request);
    }

    public static ActivationToken newEmail(String userId, String newEmail) {
        var request = Request.builder()
                .userId(userId)
                .type(ActivationTokenType.NEW_EMAIL)
                .newEmail(newEmail)
                .validity(Duration.ofHours(1))
                .build();

        return idToken(request);
    }

    public static ActivationToken passwordReset(String userId) {
        var request = Request.builder()
                .userId(userId)
                .type(ActivationTokenType.PASSWORD_RESET)
                .validity(Duration.ofHours(1))
                .build();

        return idToken(request);
    }

    public static ActivationToken unblockUser(String userId, Duration validity) {
        var request = Request.builder()
                .userId(userId)
                .type(ActivationTokenType.UNBLOCK_USER)
                .validity(validity)
                .build();

        return idToken(request);
    }

    public static ActivationToken extendSearchRequest(String userId, String searchRequestId) {
        var request = Request.builder()
                .userId(userId)
                .type(ActivationTokenType.EXTEND_SEARCH_REQUESTS)
                .linkId(searchRequestId)
                .validity(Duration.ofDays(3))
                .build();

        return idToken(request);
    }

    private static ActivationToken idToken(Request request) {
        var expiresAt = Dates.now().plus(request.validity());

        String token;
        if (request.type() == ActivationTokenType.NEW_EMAIL) {
            token = DataTokens.encoded(request.userId(), request.linkId(), request.newEmail());
        } else {
            token = DataTokens.encoded(request.userId(), request.linkId());
        }

        return new ActivationToken(request.userId(), request.type(), request.linkId(), token, expiresAt);
    }

    //Static needed for lombok builder
    private static record Request(String userId,
                           ActivationTokenType type,
                           String linkId,
                           String newEmail,
                           Duration validity) {

        @Builder
        public Request {
            if (linkId == null) {
                linkId  = ActivationTokenId.CONSTANT_LINK_ID;
            }
        }
    }
}
