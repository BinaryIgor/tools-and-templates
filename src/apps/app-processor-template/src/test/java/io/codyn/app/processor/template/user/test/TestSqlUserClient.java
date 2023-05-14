package io.codyn.app.processor.template.user.test;

import io.codyn.app.processor.template.user.core.UserState;
import io.codyn.sqldb.core.DSLContextProvider;
import io.codyn.test.TestRandom;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static io.codyn.sqldb.schema.user.tables.User.USER;

public class TestSqlUserClient {

    private final DSLContextProvider contextProvider;

    public TestSqlUserClient(DSLContextProvider contextProvider) {
        this.contextProvider = contextProvider;
    }

    public UUID createUser(User user) {
        contextProvider.context().newRecord(USER)
                .setId(user.id())
                .setName(user.name())
                .setEmail(user.email())
                .setPassword(user.password())
                .setState(user.state().name())
                .setSecondFactorAuth(user.secondFactorAuth())
                .insert();
        return user.id();
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
        return contextProvider.context()
                .selectFrom(USER)
                .where(USER.ID.eq(id))
                .fetchOptional()
                .map(r -> new User(r.getId(), r.getName(), r.getEmail(),
                        r.getPassword(),
                        UserState.valueOf(r.getState()),
                        r.getSecondFactorAuth()));
    }
}
