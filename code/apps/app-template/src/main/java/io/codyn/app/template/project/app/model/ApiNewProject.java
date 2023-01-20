package io.codyn.app.template.project.app.model;

import io.codyn.app.template.project.core.model.Project;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.UUID;

public record ApiNewProject(@Schema(description = NAME_REQUIREMENT) String name) {

    static final String NAME_REQUIREMENT = "[a-zA-Z0-9]{2,}. But you know, it's not that restrictive!";

    public Project toProject(UUID ownerId) {
        return new Project(UUID.randomUUID(), ownerId, name, 0);
    }

}
