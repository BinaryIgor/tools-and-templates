package io.codyn.app.template.project.test;

import io.codyn.app.template.project.core.ProjectRepository;
import io.codyn.app.template.project.core.model.Project;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class FakeProjectRepository implements ProjectRepository {

    private final Map<UUID, Project> projects = new HashMap<>();
    public Project savedProject;
    public UUID deletedProjectId;

    @Override
    public Project save(Project project) {
        savedProject = project;

        projects.put(project.id(), project);

        return savedProject;
    }

    @Override
    public Optional<Project> findById(UUID id) {
        return Optional.ofNullable(projects.get(id));
    }

    @Override
    public Optional<UUID> findOwnerById(UUID id) {
        return Optional.ofNullable(projects.get(id))
                .map(Project::ownerId);
    }

    @Override
    public void delete(UUID id) {
        deletedProjectId = id;
    }
}
