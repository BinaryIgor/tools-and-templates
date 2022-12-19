package io.codyn.app.template.project.app.model;

import io.codyn.app.template.project.domain.model.Project;

import java.util.UUID;

public record ApiNewProject(String name) {

    public Project toProject(UUID ownerId) {
        return new Project(UUID.randomUUID(), ownerId, name, 0);
    }

}
