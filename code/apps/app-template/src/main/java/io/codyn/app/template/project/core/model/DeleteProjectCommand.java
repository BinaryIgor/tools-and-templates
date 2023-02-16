package io.codyn.app.template.project.core.model;

import java.util.UUID;

public record DeleteProjectCommand(UUID projectId, UUID userId) {
}
