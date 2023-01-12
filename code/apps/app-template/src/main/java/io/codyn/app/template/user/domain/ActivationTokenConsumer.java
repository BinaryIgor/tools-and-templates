package io.codyn.app.template.user.domain;

import io.codyn.app.template.user.domain.model.activation.ActivationTokenId;
import io.codyn.app.template.user.domain.model.activation.ActivationTokenType;
import io.codyn.app.template.user.domain.repository.ActivationTokenRepository;
import io.codyn.types.Transactions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class ActivationTokenConsumer {

    private static final Logger log = LoggerFactory.getLogger(ActivationTokenConsumer.class);

    private final ActivationTokenRepository activationTokenRepository;
    private final TokenDataDecoder tokenDataDecoder;
    private final Transactions transactions;


    public <T> void consumeToken(String activationToken, Class<T> tokenData,
                             BiConsumer<UUID, T> onValidToken) {
        var data = tokenDataDecoder.data(activationToken);

        var tokenData = dataFromToken(data);
        var userId = tokenData.userId();
        var tokenId = new ActivationTokenId(tokenData.userId(), tokenType);

        validateToken(tokenId, activationToken);

        transactions.execute(() -> {
            if (tokenType == ActivationTokenType.NEW_EMAIL && onValidToken == null) {
                updateEmail(userId, tokenData.newEmail());
            } else {
                onValidToken.accept(userId);
            }

            activationTokenRepository.delete(tokenId);
        });
    }

    private TokenData dataFromToken(List<String> tokenData) {
        String userId;
        String newEmail;
        try {
            userId = tokenData.get(0);
            if (tokenData.size() > 1) {
                newEmail = tokenData.get(1);
            } else {
                newEmail = null;
            }
        } catch (Exception e) {
            log.warn("Invalid schema for token: {}", tokenData);
            throw HairoException.of(Errors.INVALID_ACTIVATION_TOKEN);
        }
        return new TokenData(userId, newEmail);
    }

    private void validateToken(ActivationTokenId tokenId, String receivedToken) {
        var token = activationTokenRepository.ofUser(tokenId)
                .orElseThrow(HairoException.supplierOf(Errors.ACTIVATION_TOKEN_DOES_NOT_EXIST));

        if (!receivedToken.equals(token.token())) {
            throw HairoException.of(Errors.INVALID_ACTIVATION_TOKEN);
        }

        var now = Dates.now();
        if (now.isAfter(token.expiresAt())) {
            throw HairoException.of(Errors.EXPIRED_ACTIVATION_TOKEN);
        }
    }

    private void updateEmail(UUID userId, String newEmail) {
        if (!FieldValidator.isEmailValid(newEmail)) {
            throw HairoException.of(Errors.INVALID_NEW_EMAIL);
        }

        userUpdateRepository.updateEmail(userId, newEmail);
    }

    public interface TokenDataDecoder {
        List<String> data(String token);
    }

    private record TokenData(UUID userId, String newEmail) {
    }
}
