package io.codyn.app.template.user.api;

import java.util.UUID;

public interface UserClient {

    CurrentUser currentUser();

    UUID currentUserId();
}
