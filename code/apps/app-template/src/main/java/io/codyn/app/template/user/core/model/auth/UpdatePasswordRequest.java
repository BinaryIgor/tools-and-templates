package io.codyn.app.template.user.core.model.auth;

public record UpdatePasswordRequest(String oldPassword,
                                    String newPassword) {
}
