package io.codyn.app.sockets.server.template.connection;

import java.util.UUID;

public record UserConnectionId(UUID userId,
                               String connectionId) {
}
