package io.codyn.app.template.user.app;

import io.codyn.app.template.user.api.CurrentUser;
import io.codyn.app.template.user.api.UserClient;
import org.springframework.stereotype.Component;

import java.util.UUID;

//TODO: real impl
@Component
public class FakeUserClient implements UserClient {

    private final CurrentUser user = new CurrentUser(UUID.randomUUID());

    @Override
    public CurrentUser currentUser() {
        return user;
    }

    @Override
    public UUID currentUserId() {
        return currentUser().id();
    }
}
