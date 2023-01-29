package io.codyn.app.template.user.auth.core.event;

import java.util.UUID;

public record UserCreatedEvent(UUID id, String name, String email) {
}
