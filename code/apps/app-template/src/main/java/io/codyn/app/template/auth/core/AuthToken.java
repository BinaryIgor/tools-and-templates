package io.codyn.app.template.auth.core;

import java.time.Instant;

public record AuthToken(String value, Instant expiresAt) {
}
