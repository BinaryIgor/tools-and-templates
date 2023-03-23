package io.codyn.app.sockets.server.template.message;

public record RawSocketMessage(SocketMessageType type,
                               String dataJson) {
}
