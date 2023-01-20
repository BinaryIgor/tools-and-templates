package io.codyn.app.template.project.core.model;

import java.util.UUID;

public record Project(UUID id,
                      UUID ownerId,
                      String name,
                      long version) {

    public Project withVersion(long version) {
        return new Project(id, ownerId, name, version);
    }
}
