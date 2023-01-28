package io.codyn.app.template.user.common.core;


import io.codyn.app.template.user.common.core.model.ActivationToken;
import io.codyn.app.template.user.common.core.model.ActivationTokenId;

import java.util.Optional;

public interface ActivationTokenRepository {

    void save(ActivationToken token);

    Optional<ActivationToken> ofId(ActivationTokenId id);

    void delete(ActivationTokenId id);
}
