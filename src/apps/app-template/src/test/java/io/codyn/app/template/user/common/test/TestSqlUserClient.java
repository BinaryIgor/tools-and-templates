package io.codyn.app.template.user.common.test;

import io.codyn.app.template._common.core.model.UserRole;
import io.codyn.app.template._common.core.model.UserState;
import io.codyn.app.template.user.auth.infra.SqlUserRepository;
import io.codyn.app.template.user.common.core.model.User;
import io.codyn.sqldb.core.DSLContextProvider;
import io.codyn.test.TestRandom;

import java.time.Instant;
import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

import static io.codyn.sqldb.schema.user.tables.Role.ROLE;
import static io.codyn.sqldb.schema.user.tables.User.USER;

public class TestSqlUserClient {

    private final SqlUserRepository userRepository;
    private final DSLContextProvider contextProvider;

    public TestSqlUserClient(DSLContextProvider contextProvider) {
        this.userRepository = new SqlUserRepository(contextProvider);
        this.contextProvider = contextProvider;
    }

    public UUID createUser(User user) {
        userRepository.create(user);
        return user.id();
    }

    public void changeUserState(UUID id, UserState state) {
        userRepository.updateState(id, state);
    }

    public void setUserRoles(UUID id, Collection<UserRole> roles) {
        contextProvider.context()
                .deleteFrom(ROLE)
                .where(ROLE.USER_ID.eq(id))
                .execute();

        for (var r : roles) {
            contextProvider.context()
                    .insertInto(ROLE)
                    .columns(ROLE.USER_ID, ROLE.VALUE)
                    .values(id, r.name())
                    .execute();
        }
    }

    public User createRandomUser(UUID id) {
        return createRandomUser(id, TestRandom.oneOf(UserState.values()));
    }

    public User createRandomUser(UUID id, UserState state) {
        var name = TestRandom.string(5, 30);
        var user = new User(id, name, name + "@email.com", "SomePass1234", state,
                TestRandom.isTrue());

        createUser(user);

        return user;
    }

    public void updateUserCreatedAt(UUID id, Instant createdAt) {
        contextProvider.context()
                .update(USER)
                .set(USER.CREATED_AT, createdAt)
                .where(USER.ID.eq(id))
                .execute();
    }

    public Optional<User> userOfId(UUID id) {
        return userRepository.ofId(id);
    }
}
