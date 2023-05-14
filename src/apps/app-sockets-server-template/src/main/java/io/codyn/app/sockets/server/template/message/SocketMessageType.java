package io.codyn.app.sockets.server.template.message;

public enum SocketMessageType {
    USER_AUTHENTICATION, USER_AUTHENTICATED,
    PING, PONG,
    UNKNOWN, FAILURE
}
