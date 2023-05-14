package io.codyn.outbox;

import java.time.Instant;
import java.util.UUID;

public record OutboxMessage(UUID id,
                            String target,
                            String type,
                            byte[] data,
                            Instant createdAt) {
}
