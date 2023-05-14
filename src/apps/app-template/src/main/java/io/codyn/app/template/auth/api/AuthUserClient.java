package io.codyn.app.template.auth.api;

import java.util.UUID;

public interface AuthUserClient {

    AuthenticatedUser current();

    UUID currentId();
}
