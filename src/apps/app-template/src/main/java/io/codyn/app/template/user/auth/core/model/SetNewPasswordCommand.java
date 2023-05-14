package io.codyn.app.template.user.auth.core.model;

public record SetNewPasswordCommand(String password,
                                    String token) {
}
