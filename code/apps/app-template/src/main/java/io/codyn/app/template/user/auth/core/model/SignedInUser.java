package io.codyn.app.template.user.auth.core.model;

import io.codyn.app.template.auth.core.AuthTokens;

public record SignedInUser(CurrentUserData data,
                           AuthTokens tokens) {
}
