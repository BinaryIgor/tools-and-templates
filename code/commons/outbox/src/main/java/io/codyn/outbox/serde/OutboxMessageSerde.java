package io.codyn.outbox.serde;

import io.codyn.outbox.OutboxMessage;

public interface OutboxMessageSerde {

    default <T> String messageType(T message) {
        return message.getClass().getCanonicalName();
    }

    <T> byte[] serialize(T message);

    Object deserialize(OutboxMessage message);
}
