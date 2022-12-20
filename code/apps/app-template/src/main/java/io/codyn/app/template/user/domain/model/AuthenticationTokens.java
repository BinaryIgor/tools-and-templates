package io.codyn.app.template.user.domain.model;

public record AuthenticationTokens(AuthenticationToken access,
                                   AuthenticationToken refresh) {
}
