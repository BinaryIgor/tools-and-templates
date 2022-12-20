package io.codyn.app.template.project.app.model;

import io.codyn.app.template.project.domain.model.Project;

import java.util.UUID;

public record ApiUpdateProject(String name, long version) {

    public Project toProject(UUID id, UUID ownerId) {
        return new Project(id, ownerId, name, version);
    }
}
