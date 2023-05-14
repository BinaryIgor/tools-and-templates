package io.codyn.outbox.experiments;

import java.time.Instant;
import java.util.UUID;

public record Data(UUID id, String message, Instant deadline) {
}
