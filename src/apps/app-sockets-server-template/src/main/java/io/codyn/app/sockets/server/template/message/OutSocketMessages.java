package io.codyn.app.sockets.server.template.message;

import java.util.List;

public class OutSocketMessages {

    public static SocketMessage<Failure> failure(SocketMessageType source, String... errors) {
        return failure(source, List.of(errors));
    }

    public static SocketMessage<Failure> failure(SocketMessageType source, List<String> errors) {
        return failure(source, errors, null);
    }

    public static SocketMessage<Failure> failure(SocketMessageType source, List<String> errors, String details) {
        return new SocketMessage<>(SocketMessageType.FAILURE, new Failure(source, errors, details));
    }
}
