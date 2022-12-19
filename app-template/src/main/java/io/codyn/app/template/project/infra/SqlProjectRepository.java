package io.codyn.app.template.project.infra;

import io.codyn.app.template.project.domain.ProjectRepository;
import io.codyn.app.template.project.domain.model.Project;
import io.codyn.app.template.project.infra.entity.ProjectEntity;
import io.codyn.app.template.project.infra.entity.ProjectEntityRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public class SqlProjectRepository implements ProjectRepository {

    private final ProjectEntityRepository entityRepository;

    public SqlProjectRepository(ProjectEntityRepository entityRepository) {
        this.entityRepository = entityRepository;
    }

    @Override
    public Project save(Project project) {
        var entity = ProjectEntity.fromProject(project);
        return entityRepository.save(entity).toProject();
    }

    @Override
    public Optional<Project> findById(UUID id) {
        return entityRepository.findById(id).map(ProjectEntity::toProject);
    }

    @Override
    public Optional<UUID> findOwnerById(UUID id) {
        return entityRepository.findOwnerById(id);
    }

    @Override
    public void delete(UUID id) {
        entityRepository.deleteById(id);
    }
}
