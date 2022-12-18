package io.codyn.app.template.project.infra;

import io.codyn.app.template._shared.app.JdbcTemplates;
import io.codyn.app.template.project.domain.Project;
import io.codyn.app.template.project.domain.ProjectRepository;
import io.codyn.app.template.project.infra.entity.ProjectEntity;
import io.codyn.app.template.project.infra.entity.ProjectEntityRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Repository
public class SqlProjectRepository implements ProjectRepository {

    private final ProjectEntityRepository entityRepository;
    private final JdbcTemplates jdbcTemplates;

    public SqlProjectRepository(ProjectEntityRepository entityRepository,
                                JdbcTemplates jdbcTemplates) {
        this.entityRepository = entityRepository;
        this.jdbcTemplates = jdbcTemplates;
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

    @Override
    public void addUsers(UUID id, List<UUID> userIds) {
        jdbcTemplates.template()
                .update("DELETE FROM project.project_user WHERE project_id = ?", id);
        jdbcTemplates.template()
                .batchUpdate("INSERT INTO project.project_user VALUES (?, ?)", userIds, 100,
                        (stmt, uid) -> {
                            stmt.setObject(1, id);
                            stmt.setObject(2, uid);
                        });
    }

    @Override
    public void removeUsers(UUID id, List<UUID> userIds) {
        jdbcTemplates.namedTemplate()
                .update("""
                        DELETE FROM project.project_user
                        WHERE project_id = :projectId AND user_id IN (:userIds)
                        """, Map.of("projectId", id, "userIds", userIds));
    }
}
