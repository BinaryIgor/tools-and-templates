package io.codyn.app.template.user.common.core.repository;

import io.codyn.app.template.user.common.core.model.ActivationTokenId;
import io.codyn.app.template.user.common.core.model.ActivationTokenStatus;

public interface ActivationTokenStatusUpdateRepository {
    void update(ActivationTokenId id, ActivationTokenStatus status);
}
