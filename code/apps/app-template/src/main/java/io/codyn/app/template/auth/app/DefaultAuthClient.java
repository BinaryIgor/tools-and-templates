package io.codyn.app.template.auth.app;

import io.codyn.app.template._shared.domain.exception.UnauthenticatedException;
import io.codyn.app.template.auth.api.AuthClient;
import io.codyn.app.template.auth.domain.AuthTokenComponent;
import io.codyn.app.template.auth.domain.AuthTokens;
import io.codyn.app.template.auth.domain.AuthenticatedUser;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class DefaultAuthClient implements AuthClient {

    private final AuthTokenComponent authTokenComponent;

    public DefaultAuthClient(AuthTokenComponent authTokenComponent) {
        this.authTokenComponent = authTokenComponent;
    }

    @Override
    public AuthTokens ofUser(UUID id) {
        return authTokenComponent.ofUser(id);
    }

    @Override
    public AuthTokens refresh(String refreshToken) {
        return authTokenComponent.refresh(refreshToken);
    }

    @Override
    public AuthenticatedUser authenticate(String accessToken) {
        return authTokenComponent.authenticate(accessToken);
    }

    @Override
    public AuthenticatedUser currentUser() {
        return AuthenticatedUserRequestHolder.get()
                .orElseThrow(UnauthenticatedException::new);
    }
}
