package io.codyn.app.template.project.core.model;

import java.util.UUID;

public record GetProjectWithUsersQuery(UUID projectId, UUID userId) {
}
