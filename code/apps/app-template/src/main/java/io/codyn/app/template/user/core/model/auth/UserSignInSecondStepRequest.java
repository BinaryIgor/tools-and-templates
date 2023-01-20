package io.codyn.app.template.user.core.model.auth;

public record UserSignInSecondStepRequest(String email, String code) {
}
