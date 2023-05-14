package io.codyn.app.template.user.account.core.model;

public record UpdatePasswordRequest(String oldPassword,
                                    String newPassword) {
}
