package io.codyn.app.sockets.server.template.connection;

import java.util.UUID;

//TODO: impl!
public class ApiConnectionAuthenticator implements ConnectionAuthenticator {
    @Override
    public UUID authenticate(String token) {
        return UUID.randomUUID();
    }
}
