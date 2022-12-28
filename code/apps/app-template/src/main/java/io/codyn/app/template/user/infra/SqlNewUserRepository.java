package io.codyn.app.template.user.infra;

import io.codyn.app.template.user.domain.model.NewUser;
import io.codyn.app.template.user.domain.repository.NewUserRepository;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

import java.util.UUID;

import static io.codyn.commons.sqldb.schema.user.tables.User.USER;

@Repository
public class SqlNewUserRepository implements NewUserRepository {

    private final DSLContext context;

    public SqlNewUserRepository(DSLContext context) {
        this.context = context;
    }

    @Override
    public UUID create(NewUser user) {
        var newId = UUID.randomUUID();

        UserRecordsMapper.setFromNewUser(context.newRecord(USER), user)
                .setId(newId)
                .insert();

        return newId;
    }
}
