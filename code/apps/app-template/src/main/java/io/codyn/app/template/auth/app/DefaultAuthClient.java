package io.codyn.app.template.auth.app;

import io.codyn.app.template._common.core.exception.UnauthenticatedException;
import io.codyn.app.template.auth.api.AuthClient;
import io.codyn.app.template.auth.api.AuthUserClient;
import io.codyn.app.template.auth.api.AuthenticatedUser;
import io.codyn.app.template.auth.core.AuthTokenCreator;
import io.codyn.app.template.auth.core.AuthTokens;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class DefaultAuthClient implements AuthClient, AuthUserClient {

    private final AuthTokenCreator authTokenCreator;

    public DefaultAuthClient(AuthTokenCreator authTokenCreator) {
        this.authTokenCreator = authTokenCreator;
    }

    @Override
    public AuthTokens ofUser(UUID id) {
        return authTokenCreator.ofUser(id);
    }

    @Override
    public AuthTokens refresh(String refreshToken) {
        return authTokenCreator.refresh(refreshToken);
    }

    @Override
    public AuthenticatedUser current() {
        return AuthenticatedUserRequestHolder.get()
                .orElseThrow(UnauthenticatedException::new);
    }

    @Override
    public UUID currentId() {
        return current().id();
    }
}
