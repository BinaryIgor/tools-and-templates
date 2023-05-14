package io.codyn.app.template.auth.api;

import java.util.Optional;
import java.util.UUID;

public interface UserAuthDataRepository {
    Optional<UserAuthData> ofId(UUID id);
}
