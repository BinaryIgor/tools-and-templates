package io.codyn.app.template.user.domain.model;

public record SignedInUser(CurrentUserData data,
                           AuthenticationTokens tokens) {
}
