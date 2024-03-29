package io.codyn.app.template.user.common.infra;

import io.codyn.app.template._common.core.model.UserRole;
import io.codyn.app.template._common.core.model.UserState;
import io.codyn.app.template.auth.api.UserAuthData;
import io.codyn.app.template.auth.api.UserAuthDataRepository;
import io.codyn.sqldb.core.DSLContextProvider;
import io.codyn.sqldb.core.SqlMapper;

import java.util.Optional;
import java.util.UUID;

import static io.codyn.sqldb.schema.user.Tables.ROLE;
import static io.codyn.sqldb.schema.user.Tables.USER;

public class SqlUserAuthDataRepository implements UserAuthDataRepository {

    private final DSLContextProvider contextProvider;

    public SqlUserAuthDataRepository(DSLContextProvider contextProvider) {
        this.contextProvider = contextProvider;
    }

    @Override
    public Optional<UserAuthData> ofId(UUID id) {
        var result = contextProvider.context()
                .select(USER.STATE, ROLE.VALUE)
                .from(USER)
                .leftJoin(ROLE)
                .on(USER.ID.eq(ROLE.USER_ID))
                .where(USER.ID.eq(id))
                .fetch();

        if (result.isEmpty()) {
            return Optional.empty();
        }

        var state = UserState.valueOf(result.get(0).get(USER.STATE));
        var roles = SqlMapper.nonNullFieldsSet(result, ROLE.VALUE, UserRole::valueOf);

        return Optional.of(new UserAuthData(id, state, roles));
    }
}
