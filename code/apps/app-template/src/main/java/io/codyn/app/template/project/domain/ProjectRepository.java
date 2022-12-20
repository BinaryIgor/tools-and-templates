package io.codyn.app.template.project.domain;

import io.codyn.app.template.project.domain.model.Project;

import java.util.Optional;
import java.util.UUID;

public interface ProjectRepository {
    Project save(Project project);

    Optional<Project> findById(UUID id);

    Optional<UUID> findOwnerById(UUID id);

    void delete(UUID id);
}
