package io.codyn.app.template.user.account.core.model;

import java.util.UUID;

public record UpdateUserPasswordCommand(UUID id,
                                        String oldPassword,
                                        String newPassword) {
}
