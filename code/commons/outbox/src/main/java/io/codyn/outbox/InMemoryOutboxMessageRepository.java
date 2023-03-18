package io.codyn.outbox;

import java.util.*;

public class InMemoryOutboxMessageRepository implements OutboxMessageRepository {

    private final List<OutboxMessage> messages = new ArrayList<>();

    @Override
    public void createAll(Collection<OutboxMessage> messages) {
        this.messages.addAll(messages);
    }

    @Override
    public List<OutboxMessage> allOf(Query query) {
        return messages.stream()
                .filter(m -> m.createdAt().isBefore(query.createdBefore())
                        && m.target().equals(query.target()))
                .sorted(Comparator.comparing(OutboxMessage::createdAt))
                .skip(query.offset())
                .limit(query.limit())
                .toList();
    }

    @Override
    public void deleteAll(Collection<UUID> ids) {
        messages.removeIf(m -> ids.contains(m.id()));
    }
}
