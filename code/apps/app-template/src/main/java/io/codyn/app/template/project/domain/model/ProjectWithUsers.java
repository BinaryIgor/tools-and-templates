package io.codyn.app.template.project.domain.model;

import java.util.List;
import java.util.UUID;

public record ProjectWithUsers(Project project,
                               List<UUID> users) {
}
