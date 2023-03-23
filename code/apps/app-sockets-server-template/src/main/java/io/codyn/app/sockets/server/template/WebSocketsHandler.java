package io.codyn.app.sockets.server.template;

import io.codyn.app.sockets.server.template.common.AppException;
import io.codyn.app.sockets.server.template.connection.ConnectionAuthenticator;
import io.codyn.app.sockets.server.template.connection.UserConnectionId;
import io.codyn.app.sockets.server.template.message.*;
import jakarta.annotation.PreDestroy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.PongMessage;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

//TODO ping-pong response
//TODO: shutdown hook
public class WebSocketsHandler extends TextWebSocketHandler {

    static final int MAX_USER_CONNECTIONS = 10;
    private static final Logger log = LoggerFactory.getLogger(WebSocketsHandler.class);
    private final Map<String, UUID> authenticatedConnections = new ConcurrentHashMap<>();
    private final Map<UUID, Map<String, WebSocketSession>> usersConnections = new ConcurrentHashMap<>();
    private final ConnectionAuthenticator authenticator;
    private final Map<SocketMessageType, SocketMessageHandler> messageHandlers = new HashMap<>();

    public WebSocketsHandler(ConnectionAuthenticator authenticator) {
        this.authenticator = authenticator;
    }

    public void addMessageHandler(SocketMessageType type, SocketMessageHandler handler) {
        messageHandlers.put(type, handler);
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        super.handleTransportError(session, exception);
        log.error("Transport error", exception);
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        super.afterConnectionEstablished(session);
        log.info("New connection has been established {} on a Thread: {}", session.getId(),
                Thread.currentThread());
        //TODO: connections counter!
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        super.afterConnectionClosed(session, status);
        log.info("Closing connection with {} status...", status);
        //TODO remove user conn
        closeSocket(session);
    }

    private void closeSocket(WebSocketSession socket) {
        try {
            socket.close();
        } catch (Exception e) {
            //swallow
        }
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        log.info("New text message: {}", message.getPayload());
        WebSockets.message(session, message.getPayload())
                .ifPresent(msg -> handleNewMessage(session, msg));
    }

    @Override
    protected void handlePongMessage(WebSocketSession session, PongMessage message) throws Exception {
        //intentionally ignore
    }

    private void handleNewMessage(WebSocketSession session, RawSocketMessage message) {
        var userId = authenticatedConnections.get(session.getId());

        if (message.type() == SocketMessageType.USER_AUTHENTICATION) {
            handleUserAuthentication(session, message);
        } else if (userId == null) {
            WebSockets.send(session,
                    OutSocketMessages.failure(SocketMessageType.UNKNOWN, SocketErrors.NOT_AUTHENTICATED));
        } else if (message.type() == SocketMessageType.PING) {
            handlePingMessage(session);
        } else {
            handleUserMessage(session, userId, message);
        }
    }

    private void handleUserAuthentication(WebSocketSession session, RawSocketMessage message) {
        try {
            var uid = authenticator.authenticate(message.dataJson());
            var userConnections = currentOrInitializedUserConnections(uid);

            if (userConnections.size() >= MAX_USER_CONNECTIONS) {
                WebSockets.send(session,
                        OutSocketMessages.failure(SocketMessageType.USER_AUTHENTICATED,
                                SocketErrors.USER_TOO_MANY_CONNECTIONS));
            } else {
                var cid = session.getId();
                authenticatedConnections.put(cid, uid);
                userConnections.put(cid, session);
            }
        } catch (Exception e) {
            log.warn("Failed to authenticate user", e);
        }
    }

    private Map<String, WebSocketSession> currentOrInitializedUserConnections(UUID userId) {
        return usersConnections.computeIfAbsent(userId, k -> new ConcurrentHashMap<>());
    }

    private void handlePingMessage(WebSocketSession session) {
        WebSockets.send(session, SocketMessage.empty(SocketMessageType.PONG));
    }

    private void handleUserMessage(WebSocketSession session,
                                   UUID userId,
                                   RawSocketMessage message) {
        try {
            Optional.ofNullable(messageHandlers.get(message.type()))
                    .ifPresentOrElse(
                            h -> {
                                h.handle(new UserConnectionId(userId, session.getId()), message);
                            },
                            () -> log.warn("Lacking handler for {} type", message.type()));
        } catch (AppException e) {
            log.warn("Handled AppException while handling message...", e);
            WebSockets.send(session, OutSocketMessages.failure(message.type(), e.toErrors(), e.getMessage()));
        } catch (Exception e) {
            log.error("Unhandled exception while handling message...", e);
            WebSockets.send(session, OutSocketMessages.failure(message.type(), SocketErrors.UNKNOWN_ERROR));
        }
    }

    @PreDestroy
    public void close() {
        //TODO: shutdown gracefully!
        log.info("Closing connection server...");
    }
}
