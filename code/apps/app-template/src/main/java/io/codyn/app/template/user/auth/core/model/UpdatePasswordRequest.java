package io.codyn.app.template.user.auth.core.model;

public record UpdatePasswordRequest(String oldPassword,
                                    String newPassword) {
}
