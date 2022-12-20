package io.codyn.app.template.user.api.event;

import java.util.UUID;

public record UserCreatedEvent(UUID id, String name, String email) {
}
