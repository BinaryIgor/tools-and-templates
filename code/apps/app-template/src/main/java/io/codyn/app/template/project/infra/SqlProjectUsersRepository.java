package io.codyn.app.template.project.infra;

import io.codyn.app.template._shared.app.JdbcTemplates;
import io.codyn.app.template.project.domain.ProjectUsersRepository;
import io.codyn.app.template.project.infra.entity.ProjectEntityRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Repository
public class SqlProjectUsersRepository implements ProjectUsersRepository {

    private final ProjectEntityRepository entityRepository;
    private final JdbcTemplates jdbcTemplates;

    public SqlProjectUsersRepository(ProjectEntityRepository entityRepository,
                                JdbcTemplates jdbcTemplates) {
        this.entityRepository = entityRepository;
        this.jdbcTemplates = jdbcTemplates;
    }

    @Override
    public List<UUID> usersOfProject(UUID id) {
        return entityRepository.findProjectUsersById(id);
    }

    @Override
    public void addUsers(UUID id, List<UUID> userIds) {
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
