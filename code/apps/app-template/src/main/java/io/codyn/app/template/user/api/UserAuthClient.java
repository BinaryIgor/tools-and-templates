package io.codyn.app.template.user.api;

import java.util.Optional;
import java.util.UUID;

public interface UserAuthClient {
    Optional<UserAuthData> userOfId(UUID id);
}
