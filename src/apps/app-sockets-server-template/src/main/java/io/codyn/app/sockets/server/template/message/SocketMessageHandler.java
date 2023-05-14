package io.codyn.app.sockets.server.template.message;

import io.codyn.app.sockets.server.template.common.InvalidMessageDataException;
import io.codyn.app.sockets.server.template.connection.UserConnectionId;
import io.codyn.json.JsonMapper;

import java.util.UUID;
import java.util.function.BiConsumer;

public interface SocketMessageHandler {
    void handle(UserConnectionId id, RawSocketMessage message);

    static <T> Typed<T> ofUser(Class<T> type, BiConsumer<UUID, T> handler) {
        return new Typed<>(type, ((userConnectionId, t) -> handler.accept(userConnectionId.userId(), t)));
    }

    static <T> Typed<T> ofUserConnection(Class<T> type, BiConsumer<UserConnectionId, T> handler) {
        return new Typed<>(type, handler);
    }

    class Typed<T> implements SocketMessageHandler {

        private final Class<T> type;
        private final BiConsumer<UserConnectionId, T> handler;

        public Typed(Class<T> type,
                     BiConsumer<UserConnectionId, T> handler) {
            this.type = type;
            this.handler = handler;
        }

        @Override
        public void handle(UserConnectionId id, RawSocketMessage message) {
            var data = parsedData(message.dataJson());
            handler.accept(id, data);
        }

        private T parsedData(String json) {
            try {
                return JsonMapper.object(json, type);
            } catch (Exception e) {
                throw new InvalidMessageDataException(type);
            }
        }
    }
}
