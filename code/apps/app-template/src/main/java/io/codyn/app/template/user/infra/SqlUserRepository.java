package io.codyn.app.template.user.infra;

import io.codyn.app.template._shared.domain.model.UserState;
import io.codyn.app.template.user.domain.model.User;
import io.codyn.app.template.user.domain.model.auth.NewUser;
import io.codyn.app.template.user.domain.repository.NewUserRepository;
import io.codyn.app.template.user.domain.repository.UserRepository;
import io.codyn.app.template.user.domain.repository.UserUpdateRepository;
import io.codyn.sqldb.core.DSLContextProvider;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

import static io.codyn.sqldb.schema.user.tables.User.USER;

//TODO test!
@Repository
public class SqlUserRepository implements NewUserRepository, UserRepository, UserUpdateRepository {

    private final DSLContextProvider contextProvider;

    public SqlUserRepository(DSLContextProvider contextProvider) {
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

    @Override
    public Optional<User> findByEmail(String email) {
        return contextProvider.context()
                .selectFrom(USER)
                .where(USER.EMAIL.eq(email))
                .fetchOptional(UserRecordsMapper::fromUserRecord);
    }

    @Override
    public void updateState(UUID id, UserState state) {
        contextProvider.context()
                .update(USER)
                .set(USER.STATE, state.name())
                .where(USER.ID.eq(id))
                .execute();
    }
}
