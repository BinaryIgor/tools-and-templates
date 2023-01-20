package io.codyn.app.template.user.core.api;

import io.codyn.app.template.auth.api.AuthClient;
import io.codyn.app.template.user.api.CurrentUser;
import io.codyn.app.template.user.api.UserClient;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class DefaultUserClient implements UserClient {

    private final AuthClient authClient;

    public DefaultUserClient(AuthClient authClient) {
        this.authClient = authClient;
    }

    @Override
    public CurrentUser currentUser() {
        var authUser = authClient.currentUser();
        return new CurrentUser(authUser.id());
    }

    @Override
    public UUID currentUserId() {
        return authClient.currentUser().id();
    }
}
