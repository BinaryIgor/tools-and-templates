package io.codyn.app.template.project.core.model;

import java.util.List;
import java.util.UUID;

public record AddUsersToProjectCommand(UUID projectId,
                                       UUID userId,
                                       List<UUID> toAddUserIds) {
}
