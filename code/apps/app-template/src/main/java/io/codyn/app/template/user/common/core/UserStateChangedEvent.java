package io.codyn.app.template.user.common.core;

import io.codyn.app.template._common.core.model.UserState;

import java.util.UUID;

public record UserStateChangedEvent(UUID id, UserState newState) {
}
