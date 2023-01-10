package io.codyn.app.template.user.domain;

import io.codyn.app.template.user.domain.model.activation.ActivationToken;
import io.codyn.app.template.user.domain.model.activation.ActivationTokenType;
import io.codyn.tools.DataTokens;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

public class ActivationTokenFactory {

    public static ActivationToken newUser(UUID userId, Clock clock) {
        var token = DataTokens.containing(userId.toString());

        return new ActivationToken(userId, ActivationTokenType.NEW_USER, token,
                tokenExpiresAt(clock, Duration.ofMinutes(15)));
    }

    private static Instant tokenExpiresAt(Clock clock, Duration duration) {
        return clock.instant().plus(duration);
    }

    public static ActivationToken newEmail(UUID userId, String newEmail, Clock clock) {
        var token = DataTokens.containing(userId.toString(), newEmail);

        return new ActivationToken(userId, ActivationTokenType.NEW_EMAIL, token,
                tokenExpiresAt(clock, Duration.ofHours(1)));
    }

    public static ActivationToken passwordReset(UUID userId, Clock clock) {
        var token = DataTokens.containing(userId.toString());

        return new ActivationToken(userId, ActivationTokenType.PASSWORD_RESET, token,
                tokenExpiresAt(clock, Duration.ofHours(1)));
    }
}
