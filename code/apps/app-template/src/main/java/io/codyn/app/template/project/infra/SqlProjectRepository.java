package io.codyn.app.template.project.infra;

import io.codyn.app.template._common.infra.SqlOptimisticLock;
import io.codyn.app.template.project.core.ProjectRepository;
import io.codyn.app.template.project.core.model.Project;
import io.codyn.sqldb.schema.project.tables.records.ProjectRecord;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

import static io.codyn.sqldb.schema.project.Tables.PROJECT;

@Repository
public class SqlProjectRepository implements ProjectRepository {

    private final DSLContext context;

    public SqlProjectRepository(DSLContext context) {
        this.context = context;
    }

    @Override
    public Project save(Project project) {
        var newVersionProject = project.withVersion(project.version() + 1);

        var record = ProjectRecordsMapper.setFromProject(new ProjectRecord(), newVersionProject);

        SqlOptimisticLock.upsert(context, PROJECT, record);

        return newVersionProject;
    }


    @Override
    public Optional<Project> findById(UUID id) {
        return context.selectFrom(PROJECT).where(PROJECT.ID.eq(id))
                .fetchOptional(ProjectRecordsMapper::fromProjectRecord);
    }

    @Override
    public Optional<UUID> findOwnerById(UUID id) {
        return context.select(PROJECT.OWNER_ID)
                .from(PROJECT)
                .where(PROJECT.ID.eq(id))
                .fetchOptional(PROJECT.OWNER_ID);
    }

    @Override
    public void delete(UUID id) {
        context.deleteFrom(PROJECT)
                .where(PROJECT.ID.eq(id))
                .execute();
    }
}
