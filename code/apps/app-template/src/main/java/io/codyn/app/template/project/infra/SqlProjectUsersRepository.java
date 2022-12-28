package io.codyn.app.template.project.infra;

import io.codyn.app.template.project.domain.ProjectUsersRepository;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

import static io.codyn.commons.sqldb.schema.project.Tables.PROJECT_USER;

@Repository
public class SqlProjectUsersRepository implements ProjectUsersRepository {

    private final DSLContext context;

    public SqlProjectUsersRepository(DSLContext context) {
        this.context = context;
    }

    @Override
    public List<UUID> usersOfProject(UUID id) {
        return context.select(PROJECT_USER.USER_ID)
                .from(PROJECT_USER)
                .where(PROJECT_USER.PROJECT_ID.eq(id))
                .fetch(PROJECT_USER.USER_ID);
    }

    @Override
    public void addUsers(UUID id, List<UUID> userIds) {
        var insert = context.insertInto(PROJECT_USER)
                .columns(PROJECT_USER.PROJECT_ID, PROJECT_USER.USER_ID);

        userIds.forEach(uid -> insert.values(id, uid));

        insert.execute();
    }

    @Override
    public void removeUsers(UUID id, List<UUID> userIds) {
        context.deleteFrom(PROJECT_USER)
                .where(PROJECT_USER.PROJECT_ID.eq(id)
                        .and(PROJECT_USER.USER_ID.in(userIds)))
                .execute();
    }
}
