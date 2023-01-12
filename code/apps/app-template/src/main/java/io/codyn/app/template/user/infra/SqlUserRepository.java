package io.codyn.app.template.user.infra;

import io.codyn.app.template.user.domain.model.User;
import io.codyn.app.template.user.domain.repository.UserRepository;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

import java.util.Optional;

import static io.codyn.sqldb.schema.user.Tables.USER;

@Repository
public class SqlUserRepository implements UserRepository {

    private final DSLContext context;

    public SqlUserRepository(DSLContext context) {
        this.context = context;
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return context.selectFrom(USER)
                .where(USER.EMAIL.eq(email))
                .fetchOptional(UserRecordsMapper::fromUserRecord);
    }
}
