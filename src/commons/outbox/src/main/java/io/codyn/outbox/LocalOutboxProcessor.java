package io.codyn.outbox;

import io.codyn.outbox.serde.OutboxMessageSerde;
import io.codyn.types.event.LocalPublisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

//TODO: is leader?
public class LocalOutboxProcessor {

    private static final Logger log = LoggerFactory.getLogger(LocalOutboxProcessor.class);

    private final LocalPublisher localPublisher;
    private final OutboxMessageRepository outboxMessageRepository;
    private final OutboxMessageSerde outboxMessageSerde;
    private final String consumer;
    private final int batchSize;

    public LocalOutboxProcessor(LocalPublisher localPublisher,
                                OutboxMessageRepository outboxMessageRepository,
                                OutboxMessageSerde outboxMessageSerde,
                                String consumer, int batchSize) {
        this.localPublisher = localPublisher;
        this.outboxMessageRepository = outboxMessageRepository;
        this.outboxMessageSerde = outboxMessageSerde;
        this.consumer = consumer;
        this.batchSize = batchSize;
    }

    public void process() {
        var offset = 0;

        while (true) {
            var now = Instant.now();
            var query = new OutboxMessageRepository.Query(now, consumer, offset, batchSize);
            var toPublish = outboxMessageRepository.allOf(query);

            if (toPublish.isEmpty()) {
                break;
            }

            publishAll(toPublish);

            offset += batchSize;
        }
    }

    private void publishAll(List<OutboxMessage> toPublish) {
        var consumedIds = new ArrayList<UUID>();
        var failedIds = new ArrayList<UUID>();

        for (var m : toPublish) {
            try {
                var deserialized = outboxMessageSerde.deserialize(m);
                localPublisher.publish(deserialized);
                consumedIds.add(m.id());
            } catch (Exception e) {
                e.printStackTrace();
                log.warn("Problems while publishing {} message of {} type...", m.id(), m.type(), e);
                failedIds.add(m.id());
            }
        }

        if (!failedIds.isEmpty()) {
            log.error("Failed to publish {} messages", failedIds.size());
        }

        outboxMessageRepository.deleteAll(consumedIds);
    }

}
