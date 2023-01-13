package io.codyn.app.template.user.domain.component;

import io.codyn.app.template._shared.domain.exception.ResourceNotFoundException;
import io.codyn.app.template.user.domain.exception.InvalidActivationTokenException;
import io.codyn.app.template.user.domain.model.activation.ActivationTokenId;
import io.codyn.app.template.user.domain.model.activation.ActivationTokenType;
import io.codyn.app.template.user.domain.repository.ActivationTokenRepository;
import io.codyn.tools.DataTokens;
import io.codyn.types.Transactions;
import org.springframework.stereotype.Component;

import java.time.Clock;
import java.util.UUID;
import java.util.function.Consumer;

@Component
//TODO: tests
public class ActivationTokenConsumer {


    private final ActivationTokenRepository activationTokenRepository;
    private final Transactions transactions;
    private final Clock clock;

    public ActivationTokenConsumer(ActivationTokenRepository activationTokenRepository,
                                   Transactions transactions,
                                   Clock clock) {
        this.activationTokenRepository = activationTokenRepository;
        this.transactions = transactions;
        this.clock = clock;
    }

    public void consume(String activationToken, ActivationTokenType tokenType, Consumer<UUID> onValidToken) {
        consumeWithData(activationToken, tokenType, data -> onValidToken.accept(data.userId()));
    }


    public void consumeWithData(String activationToken,
                                ActivationTokenType tokenType,
                                Consumer<ActivationTokenData> onValidToken) {
        var decodedToken = DataTokens.decoded(activationToken)
                .orElseThrow(() -> InvalidActivationTokenException.ofToken(activationToken));

        var tokenData = DataTokens.extractedData(decodedToken, ActivationTokenData.class);

        var userId = tokenData.userId();
        var tokenId = new ActivationTokenId(userId, tokenType);

        validateToken(tokenId, activationToken);

        transactions.execute(() -> {
            onValidToken.accept(tokenData);
            activationTokenRepository.delete(tokenId);
        });
    }

    private void validateToken(ActivationTokenId id, String receivedToken) {
        var token = activationTokenRepository.ofId(id)
                .orElseThrow(() -> ResourceNotFoundException.ofId("ActivationToken", id));

        if (!receivedToken.equals(token.token())) {
            throw InvalidActivationTokenException.ofToken(id, "Received token is not equal to saved one");
        }

        if (clock.instant().isAfter(token.expiresAt())) {
            throw InvalidActivationTokenException.ofToken(id, "Token has expired");
        }
    }
}
