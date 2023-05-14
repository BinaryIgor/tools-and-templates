package io.codyn.app.processor.template.user.infra;

import io.codyn.app.processor.template.user.core.UserDeleteRepository;
import io.codyn.app.processor.template.user.core.UserState;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

import java.time.Instant;

import static io.codyn.sqldb.schema.user.tables.User.USER;

@Repository
public class SqlUserDeleteRepository implements UserDeleteRepository {

    private final DSLContext context;

    public SqlUserDeleteRepository(DSLContext context) {
        this.context = context;
    }

    @Override
    public void deleteAllNotActivatedCreatedBefore(Instant before) {
        context.deleteFrom(USER)
                .where(USER.STATE.eq(UserState.CREATED.name())
                        .and(USER.CREATED_AT.lessThan(before)))
                .execute();
    }
}
