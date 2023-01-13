package io.codyn.app.template.user.domain.component;

import io.codyn.app.template.user.domain.model.activation.ActivationToken;
import io.codyn.app.template.user.domain.model.activation.ActivationTokenType;
import io.codyn.tools.DataTokens;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

@Component
public class ActivationTokenFactory {

    private final TokenFactory tokenFactory;
    private final Clock clock;

    public ActivationTokenFactory(TokenFactory tokenFactory,
                                  Clock clock) {
        this.tokenFactory = tokenFactory;
        this.clock = clock;
    }

    @Autowired
    public ActivationTokenFactory(Clock clock) {
        this(DataTokens::containing, clock);
    }

    public ActivationToken newUser(UUID userId) {
        var token = tokenFactory.newToken(ActivationTokenData.withUserId(userId));

        return new ActivationToken(userId, ActivationTokenType.NEW_USER, token,
                tokenExpiresAt(Duration.ofMinutes(15)));
    }

    private Instant tokenExpiresAt(Duration duration) {
        return clock.instant().plus(duration);
    }

    public ActivationToken newEmail(UUID userId, String newEmail) {
        var token = tokenFactory.newToken(ActivationTokenData.withUserIdAndNewEmail(userId, newEmail));

        return new ActivationToken(userId, ActivationTokenType.NEW_EMAIL, token,
                tokenExpiresAt(Duration.ofHours(1)));
    }

    public ActivationToken passwordReset(UUID userId) {
        var token = tokenFactory.newToken(ActivationTokenData.withUserId(userId));

        return new ActivationToken(userId, ActivationTokenType.PASSWORD_RESET, token,
                tokenExpiresAt(Duration.ofHours(1)));
    }

    public interface TokenFactory {
        String newToken(ActivationTokenData data);
    }
}
