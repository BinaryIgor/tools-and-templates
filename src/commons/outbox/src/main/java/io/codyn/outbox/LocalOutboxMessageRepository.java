package io.codyn.outbox;

import io.codyn.outbox.serde.OutboxMessageSerde;

import java.time.Clock;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class LocalOutboxMessageRepository {

    private final OutboxMessageRepository repository;
    private final OutboxMessageSerde outboxMessageSerde;
    private final Map<Class<?>, Collection<String>> messagesConsumers;
    private final List<String> defaultConsumerAsList;
    private final Clock clock;

    public LocalOutboxMessageRepository(OutboxMessageRepository repository,
                                        OutboxMessageSerde outboxMessageSerde,
                                        Map<Class<?>, Collection<String>> messagesConsumers,
                                        String defaultConsumer,
                                        Clock clock) {
        this.repository = repository;
        this.outboxMessageSerde = outboxMessageSerde;
        this.messagesConsumers = messagesConsumers;
        this.defaultConsumerAsList = List.of(defaultConsumer);
        this.clock = clock;
    }

    public <T> void create(T message) {
        var messagesToCreate = prepareMessagesToCreate(message);
        repository.createAll(messagesToCreate);
    }

    private <T> List<OutboxMessage> prepareMessagesToCreate(T message) {
        var serializedMessage = outboxMessageSerde.serialize(message);
        var consumers = messagesConsumers.getOrDefault(message.getClass(), defaultConsumerAsList);
        var messageType = outboxMessageSerde.messageType(message);

        return consumers.stream()
                .map(c -> newOutboxMessage(c, messageType, serializedMessage))
                .toList();
    }

    public <T> void createAll(Collection<T> messages) {
        if (messages.isEmpty()) {
            return;
        }

        var messagesToCreate = messages.stream()
                .flatMap(m -> prepareMessagesToCreate(m).stream())
                .toList();

        repository.createAll(messagesToCreate);
    }

    private OutboxMessage newOutboxMessage(String consumer, String type, byte[] data) {
        return new OutboxMessage(UUID.randomUUID(), consumer, type, data, clock.instant());
    }
}
