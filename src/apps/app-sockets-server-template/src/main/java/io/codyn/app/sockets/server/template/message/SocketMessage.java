package io.codyn.app.sockets.server.template.message;

public record SocketMessage<T>(SocketMessageType type, T data) {

    public static SocketMessage<Object> empty(SocketMessageType type) {
        return new SocketMessage<>(type, null);
    }
}
