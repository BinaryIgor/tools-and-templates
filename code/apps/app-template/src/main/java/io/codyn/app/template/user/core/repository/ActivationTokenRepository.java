package io.codyn.app.template.user.core.repository;


import io.codyn.app.template.user.core.model.activation.ActivationToken;
import io.codyn.app.template.user.core.model.activation.ActivationTokenId;

import java.util.Optional;

public interface ActivationTokenRepository {

    void save(ActivationToken token);

    Optional<ActivationToken> ofId(ActivationTokenId id);

    void delete(ActivationTokenId id);
}
