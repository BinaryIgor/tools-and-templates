package io.codyn.app.sockets.server.template;

import io.codyn.app.sockets.server.template.message.OutSocketMessages;
import io.codyn.app.sockets.server.template.message.RawSocketMessage;
import io.codyn.app.sockets.server.template.message.SocketMessage;
import io.codyn.app.sockets.server.template.message.SocketMessageType;
import io.codyn.json.JsonMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;

public class WebSockets {

    private static final Logger log = LoggerFactory.getLogger(WebSockets.class);

    public static void send(WebSocketSession socket, SocketMessage<?> message) {
        send(socket, message, t -> {
            if (Objects.nonNull(t.getMessage()) && t.getMessage().contains("is closed")) {
                log.info("Socket ({}:{}) was left by the client, closing it. Related exception:",
                        socket.getId(), socket.getRemoteAddress(), t);
                closeSafely(socket);
            } else {
                log.error("Failed to send socket message", t);
            }
        });
    }

    private static void closeSafely(WebSocketSession socket) {
        try {
            socket.close();
        } catch (Exception ignored) {

        }
    }

    public static void send(WebSocketSession session, SocketMessage<?> message, Consumer<Throwable> onFailure) {
        try {
            session.sendMessage(toTextMessage(message));
        } catch (Exception e) {
            log.error("Failure to write socket message", e);
        }
    }

    private static TextMessage toTextMessage(SocketMessage<?> message) {
        var json = JsonMapper.json(message.data());
        var formattedMessage = message.type() + "\n\n" + json;
        return new TextMessage(formattedMessage);
    }

    public static Optional<RawSocketMessage> message(WebSocketSession socket, String message) {
        try {
            var typeMessage = message.split("\n\n", 1);
            var type = SocketMessageType.valueOf(typeMessage[0]);
            var json = message.length() > 1 ? typeMessage[1] : "";
            return Optional.of(new RawSocketMessage(type, json));
        } catch (Exception e) {
            send(socket, OutSocketMessages.failure(SocketMessageType.UNKNOWN, SocketErrors.INVALID_MESSAGE_FORMAT));
            return Optional.empty();
        }
    }
}
