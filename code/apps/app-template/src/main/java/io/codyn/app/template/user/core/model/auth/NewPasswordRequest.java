package io.codyn.app.template.user.core.model.auth;

public record NewPasswordRequest(String password,
                                 String token) {
}
