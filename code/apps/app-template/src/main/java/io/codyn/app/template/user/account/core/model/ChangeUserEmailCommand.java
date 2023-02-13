package io.codyn.app.template.user.account.core.model;

import java.util.UUID;

public record ChangeUserEmailCommand(UUID id, String email) {
}
