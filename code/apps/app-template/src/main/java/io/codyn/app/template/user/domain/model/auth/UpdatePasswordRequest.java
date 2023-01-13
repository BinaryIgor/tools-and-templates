package io.codyn.app.template.user.domain.model.auth;

public record UpdatePasswordRequest(String oldPassword,
                                    String newPassword) {
}
