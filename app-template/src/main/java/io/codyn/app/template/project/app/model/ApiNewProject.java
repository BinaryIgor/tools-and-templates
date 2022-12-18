package io.codyn.app.template.project.app.model;

import io.codyn.app.template._shared.domain.UUIDS;
import io.codyn.app.template.project.domain.Project;

import java.util.UUID;

public record ApiNewProject(String name) {

    public Project toProject(UUID ownerId) {
        return new Project(UUIDS.newId(), ownerId, name, 0);
    }

}
