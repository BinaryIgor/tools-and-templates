package io.codyn.app.template.user.infra;

import io.codyn.app.template.user.domain.model.auth.NewUser;
import io.codyn.app.template.user.domain.repository.NewUserRepository;
import io.codyn.commons.sqldb.core.DSLContextProvider;
import org.springframework.stereotype.Repository;

import java.util.UUID;

import static io.codyn.commons.sqldb.schema.user.tables.User.USER;

@Repository
public class SqlNewUserRepository implements NewUserRepository {

    private final DSLContextProvider contextProvider;

    public SqlNewUserRepository(DSLContextProvider contextProvider) {
        this.contextProvider = contextProvider;
    }

    @Override
    public UUID create(NewUser user) {
        var newId = UUID.randomUUID();

        UserRecordsMapper.setFromNewUser(contextProvider.context().newRecord(USER), user)
                .setId(newId)
                .insert();

        return newId;
    }
}
