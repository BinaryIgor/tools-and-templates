package io.codyn.app.template.auth.core;

import io.codyn.app.template.auth.api.AuthenticatedUser;

public interface AuthTokenAuthenticator {
    AuthenticatedUser authenticate(String accessToken);
}
