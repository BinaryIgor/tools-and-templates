package io.codyn.app.template.user.common.core.repository;


import io.codyn.app.template.user.common.core.model.ActivationToken;
import io.codyn.app.template.user.common.core.model.ActivationTokenId;

import java.util.Optional;
//TODO: update status

public interface ActivationTokenRepository {

    void save(ActivationToken token);

    Optional<ActivationToken> ofId(ActivationTokenId id);

    void delete(ActivationTokenId id);
}
