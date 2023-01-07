package io.codyn.app.template.auth.domain;

public record AuthTokens(AuthToken access,
                         AuthToken refresh) {
}
