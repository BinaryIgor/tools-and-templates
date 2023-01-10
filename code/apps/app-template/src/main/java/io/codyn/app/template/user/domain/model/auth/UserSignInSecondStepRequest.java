package io.codyn.app.template.user.domain.model.auth;

public record UserSignInSecondStepRequest(String email, String code) {
}
