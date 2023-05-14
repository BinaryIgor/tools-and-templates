package io.codyn.app.sockets.server.template.connection;

import java.util.UUID;

public interface ConnectionAuthenticator {
    UUID authenticate(String token);
}
