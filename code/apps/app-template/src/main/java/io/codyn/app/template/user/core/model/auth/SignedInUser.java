package io.codyn.app.template.user.core.model.auth;

import io.codyn.app.template.auth.core.AuthTokens;
import io.codyn.app.template.user.core.model.CurrentUserData;

public record SignedInUser(CurrentUserData data,
                           AuthTokens tokens) {
}
