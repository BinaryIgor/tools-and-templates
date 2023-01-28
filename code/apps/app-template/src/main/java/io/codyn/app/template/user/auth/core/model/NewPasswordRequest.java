package io.codyn.app.template.user.auth.core.model;

public record NewPasswordRequest(String password,
                                 String token) {
}
