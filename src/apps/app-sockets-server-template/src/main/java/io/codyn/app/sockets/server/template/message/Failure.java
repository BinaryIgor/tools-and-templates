package io.codyn.app.sockets.server.template.message;

import java.util.List;

public record Failure(SocketMessageType source, List<String> errors, String details) {
}
