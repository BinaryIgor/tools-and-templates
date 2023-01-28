package io.codyn.app.template.user.account.core;

public record UpdatePasswordRequest(String oldPassword,
                                    String newPassword) {
}
