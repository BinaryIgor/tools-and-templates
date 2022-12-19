package io.codyn.app.template.project.domain.model;

import java.util.UUID;

public record Project(UUID id,
                      UUID ownerId,
                      String name,
                      long version) {
}
