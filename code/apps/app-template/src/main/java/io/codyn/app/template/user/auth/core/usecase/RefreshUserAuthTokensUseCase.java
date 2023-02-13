package io.codyn.app.template.user.auth.core.usecase;

import io.codyn.app.template.auth.api.AuthClient;
import io.codyn.app.template.auth.core.AuthTokens;
import org.springframework.stereotype.Component;

@Component
public class RefreshUserAuthTokensUseCase {

    private final AuthClient authClient;

    public RefreshUserAuthTokensUseCase(AuthClient authClient) {
        this.authClient = authClient;
    }

    public AuthTokens handle(String refreshToken) {
        return authClient.refresh(refreshToken);
    }
}
