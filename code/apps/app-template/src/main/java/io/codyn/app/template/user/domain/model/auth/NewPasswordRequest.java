package io.codyn.app.template.user.domain.model.auth;

public record NewPasswordRequest(String password,
                                 String token) {
}
