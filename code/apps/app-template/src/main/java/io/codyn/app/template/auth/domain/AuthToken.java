package io.codyn.app.template.auth.domain;

import java.time.Instant;

public record AuthToken(String value, Instant expiresAt) {
}
