package io.codyn.outbox;

import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

public interface OutboxMessageRepository {

    void createAll(Collection<OutboxMessage> messages);

    List<OutboxMessage> allOf(Query query);

    void deleteAll(Collection<UUID> ids);

    record Query(Instant createdBefore,
                 String target,
                 int offset,
                 int limit) {
    }
}
