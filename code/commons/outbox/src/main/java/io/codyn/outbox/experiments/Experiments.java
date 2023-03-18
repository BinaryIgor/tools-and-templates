package io.codyn.outbox.experiments;

import io.codyn.outbox.InMemoryOutboxMessageRepository;
import io.codyn.outbox.LocalOutboxMessageRepository;
import io.codyn.outbox.LocalOutboxProcessor;
import io.codyn.outbox.serde.JsonOutboxMessageSerde;
import io.codyn.outbox.serde.SerializableOutboxMessageSerde;
import io.codyn.types.event.InMemoryEvents;

import java.io.Serializable;
import java.time.Clock;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class Experiments {

    public static void main(String[] args) throws Exception {
        var events = new InMemoryEvents();

        var repository = new InMemoryOutboxMessageRepository();

        var consumer = "outbox";

        var serializableSerde = new JsonOutboxMessageSerde(Map.of("Data", Data.class));

        var processor = new LocalOutboxProcessor(events.publisher(), repository, serializableSerde, consumer, 100);

        var localRepository = new LocalOutboxMessageRepository(repository, serializableSerde,
                Map.of(Data.class, List.of(consumer, "outbox-2")), consumer,
                Clock.systemUTC());

        events.subscribe(Data.class, d -> {
            System.out.println("Received!" + d);
            System.out.println("....");
        });

        while (true) {
            localRepository.create(new Data(UUID.randomUUID(), "some-name", Instant.EPOCH));
            processor.process();
            Thread.sleep(5000);
        }
    }

}
