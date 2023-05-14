package io.codyn.app.template.user.account.core.model;

import java.util.UUID;

public record ConfirmUserEmailChangeCommand(UUID id, String token) {
}
