package io.codyn.app.template.auth.core;

public record AuthTokens(AuthToken access,
                         AuthToken refresh) {
}
