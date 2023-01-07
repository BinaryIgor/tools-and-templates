package io.codyn.app.template.project.infra;

import io.codyn.app.template._shared.domain.exception.OptimisticLockException;
import io.codyn.app.template.project.domain.ProjectRepository;
import io.codyn.app.template.project.domain.model.Project;
import io.codyn.commons.sqldb.schema.project.tables.records.ProjectRecord;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

import static io.codyn.commons.sqldb.schema.project.Tables.PROJECT;

@Repository
public class SqlProjectRepository implements ProjectRepository {

    private final DSLContext context;

    public SqlProjectRepository(DSLContext context) {
        this.context = context;
    }

    @Override
    public Project save(Project project) {
        var newVersionProject = project.withVersion(project.version() + 1);

        if (project.version() == 0) {
            var record = ProjectRecordsMapper.setFromProject(context.newRecord(PROJECT), newVersionProject);
            record.insert();
        } else {
            var record = ProjectRecordsMapper.setFromProject(new ProjectRecord(), newVersionProject);

            var updated = context.update(PROJECT)
                    .set(record)
                    .where(PROJECT.ID.eq(project.id())
                            .and(PROJECT.VERSION.eq(project.version())))
                    .execute();

            if (updated == 0) {
                throw new OptimisticLockException("project");
            }
        }

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
