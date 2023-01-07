package io.codyn.app.template.user.domain.model;

public record NewPasswordRequest(String password,
                                 String token) {
}
