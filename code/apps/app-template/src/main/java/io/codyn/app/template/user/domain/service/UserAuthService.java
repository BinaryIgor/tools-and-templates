package io.codyn.app.template.user.domain.service;

import io.codyn.app.template.auth.api.AuthClient;
import io.codyn.app.template.auth.domain.AuthTokens;
import io.codyn.app.template.user.domain.model.auth.SignedInUser;
import io.codyn.app.template.user.domain.model.auth.SignedInUserStep;
import io.codyn.app.template.user.domain.model.auth.UserSignInRequest;
import io.codyn.app.template.user.domain.model.auth.UserSignInSecondStepRequest;
import org.springframework.stereotype.Service;

@Service
public class UserAuthService {

    private final AuthClient authClient;

    public UserAuthService(AuthClient authClient) {
        this.authClient = authClient;
    }

    public SignedInUserStep authenticate(UserSignInRequest request) {
        return null;
    }

    //TODO: impl
    public SignedInUser authenticateSecondStep(UserSignInSecondStepRequest request) {
        return null;
    }

    public AuthTokens newTokens(String refreshToken) {
        return authClient.refresh(refreshToken);
    }
}
