package io.codyn.app.template.user.auth.infra;

import io.codyn.app.template._common.core.model.UserRole;
import io.codyn.app.template._common.core.model.UserState;
import io.codyn.app.template.user.auth.core.model.User;
import io.codyn.app.template.user.auth.core.repository.UserAuthRepository;
import io.codyn.app.template.user.auth.core.repository.UserRepository;
import io.codyn.app.template.user.auth.core.repository.UserUpdateRepository;
import io.codyn.app.template.user.common.infra.UserRecordsMapper;
import io.codyn.sqldb.core.DSLContextProvider;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

import static io.codyn.sqldb.schema.user.tables.Role.ROLE;
import static io.codyn.sqldb.schema.user.tables.User.USER;

@Repository
public class SqlUserRepository implements UserRepository, UserUpdateRepository, UserAuthRepository {

    private final DSLContextProvider contextProvider;

    public SqlUserRepository(DSLContextProvider contextProvider) {
        this.contextProvider = contextProvider;
    }

    @Override
    public UUID create(User user) {
        UserRecordsMapper.setFromUser(contextProvider.context().newRecord(USER), user)
                .insert();
        return user.id();
    }

    @Override
    public Optional<User> ofEmail(String email) {
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

    @Override
    public void updatePassword(UUID id, String password) {
        contextProvider.context()
                .update(USER)
                .set(USER.PASSWORD, password)
                .where(USER.ID.eq(id))
                .execute();
    }

    @Override
    public Collection<UserRole> rolesOfUser(UUID id) {
        return contextProvider.context()
                .select(ROLE.VALUE)
                .from(ROLE)
                .where(ROLE.USER_ID.eq(id))
                .fetch(r -> UserRole.valueOf(r.value1()));
    }
}
