package io.codyn.app.template.project.domain;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProjectRepository {
    Project save(Project project);

    Optional<Project> findById(UUID id);

    Optional<UUID> findOwnerById(UUID id);

    void delete(UUID id);

    void addUsers(UUID id, List<UUID> userIds);

    void removeUsers(UUID id, List<UUID> userIds);
}
