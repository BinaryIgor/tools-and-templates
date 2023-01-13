package io.codyn.app.template.user.infra;

import io.codyn.app.template._shared.domain.model.UserRole;
import io.codyn.app.template._shared.domain.model.UserRoles;
import io.codyn.app.template._shared.domain.model.UserState;
import io.codyn.app.template.user.domain.model.User;
import io.codyn.app.template.user.domain.model.auth.NewUser;
import io.codyn.app.template.user.domain.model.auth.ToSignInUser;
import io.codyn.app.template.user.domain.repository.NewUserRepository;
import io.codyn.app.template.user.domain.repository.UserAuthRepository;
import io.codyn.app.template.user.domain.repository.UserRepository;
import io.codyn.app.template.user.domain.repository.UserUpdateRepository;
import io.codyn.sqldb.core.DSLContextProvider;
import io.codyn.sqldb.core.SqlMapper;
import org.jooq.Record;
import org.jooq.Result;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

import static io.codyn.sqldb.schema.user.tables.Role.ROLE;
import static io.codyn.sqldb.schema.user.tables.User.USER;

@Repository
public class SqlUserRepository implements NewUserRepository, UserRepository,
        UserUpdateRepository, UserAuthRepository {

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
    public Optional<ToSignInUser> toSignInUserOfEmail(String email) {
        var result = contextProvider.context()
                .select(USER.ID, USER.NAME, USER.EMAIL, USER.STATE,
                        USER.PASSWORD, USER.SECOND_FACTOR_AUTHENTICATION,
                        ROLE.VALUE)
                .from(USER)
                .leftJoin(ROLE)
                .on(USER.ID.eq(ROLE.ID))
                .where(USER.EMAIL.eq(email))
                .fetch();

        if (result.isEmpty()) {
            return Optional.empty();
        }

        return Optional.of(toSignInUser(result));
    }

    private ToSignInUser toSignInUser(Result<? extends Record> result) {
        var first = result.get(0);
        var roles = SqlMapper.nonNullFieldsSet(result, ROLE.VALUE, UserRole::valueOf);

        return new ToSignInUser(first.get(USER.ID), first.get(USER.NAME),
                first.get(USER.EMAIL), UserState.valueOf(first.get(USER.STATE)),
                first.get(USER.PASSWORD),
                first.get(USER.SECOND_FACTOR_AUTHENTICATION),
                UserRoles.of(roles));
    }
}
