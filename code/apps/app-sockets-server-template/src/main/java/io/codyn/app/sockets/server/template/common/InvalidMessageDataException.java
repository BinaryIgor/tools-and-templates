package io.codyn.app.sockets.server.template.common;

public class InvalidMessageDataException extends AppException {

    public InvalidMessageDataException(Class<?> type) {
        super("Invalid message data. %s type was required, but it can't be parsed".formatted(type));
    }
}
