package io.codyn.app.template.user.common.core;

import io.codyn.app.template._common.core.email.Emails;
import io.codyn.app.template.user.common.core.model.ActivationTokenId;
import io.codyn.app.template.user.common.core.model.ActivationTokenStatus;
import io.codyn.app.template.user.common.core.model.ActivationTokenType;
import io.codyn.app.template.user.common.core.repository.ActivationTokenStatusUpdateRepository;
import io.codyn.types.Pair;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Component
//TODO: tests
public class UserEmailStatusHandler {

    private final ActivationTokenStatusUpdateRepository statusUpdateRepository;

    public UserEmailStatusHandler(ActivationTokenStatusUpdateRepository statusUpdateRepository) {
        this.statusUpdateRepository = statusUpdateRepository;
    }

    public void handleDelivery(Map<String, String> emailMetadata) {
        updateActivationTokenStatusIfNeeded(emailMetadata, ActivationTokenStatus.DELIVERED);
    }

    private Optional<Pair<UUID, ActivationTokenType>> extractedUserIdAndTokenType(Map<String, String> metadata) {
        return Emails.Metadata.userId(metadata)
                .flatMap(uid ->
                        Emails.Metadata.activationTokenType(metadata)
                                .map(t -> new Pair<>(uid, t)));
    }

    private void updateActivationTokenStatusIfNeeded(Map<String, String> emailMetadata,
                                                     ActivationTokenStatus newStatus) {
        extractedUserIdAndTokenType(emailMetadata)
                .ifPresent(p -> {
                    var userId = p.first();
                    var tokenType = p.second();
                    statusUpdateRepository.update(new ActivationTokenId(userId, tokenType), newStatus);
                });
    }

    public void handleBounce(Map<String, String> emailMetadata) {
        updateActivationTokenStatusIfNeeded(emailMetadata, ActivationTokenStatus.DELIVERY_FAILURE);
    }
}
