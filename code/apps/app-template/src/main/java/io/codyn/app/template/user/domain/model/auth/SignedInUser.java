package io.codyn.app.template.user.domain.model.auth;

import io.codyn.app.template.auth.domain.AuthTokens;
import io.codyn.app.template.user.domain.model.CurrentUserData;

public record SignedInUser(CurrentUserData data,
                           AuthTokens tokens) {
}
