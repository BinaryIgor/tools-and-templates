package io.codyn.app.template.user.domain.model;

public record UserSignInSecondStepRequest(String email, String code) {
}
