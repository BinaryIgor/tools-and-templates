package io.codyn.app.template.user.infra;

import io.codyn.app.template._shared.domain.model.UserState;
import io.codyn.app.template.user.domain.model.User;
import io.codyn.app.template.user.domain.model.auth.NewUser;
import io.codyn.app.template.user.test.TestUserObjects;
import io.codyn.sqldb.test.DbIntegrationTest;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static io.codyn.sqldb.schema.user.tables.User.USER;

public class SqlUserRepositoryTest extends DbIntegrationTest {

    private SqlUserRepository repository;

    @Override
    protected void setup() {
        repository = new SqlUserRepository(contextProvider);
    }

    @Test
    void shouldCreateUser() {
        var newUser = TestUserObjects.newUser();

        var id = repository.create(newUser);

        Assertions.assertThat(newUserOfId(id))
                .isEqualTo(newUser);
    }

    @Test
    void shouldFindUserByEmail() {
        var users = TestUserObjects.users();

        users.forEach(this::createUser);

        users.forEach(u -> {
            Assertions.assertThat(repository.ofEmail(u.email()))
                    .get()
                    .isEqualTo(u);
        });
    }

    @Test
    void shouldUpdateUserState() {
        var firstUserId = repository.create(TestUserObjects.newUser1());
        var secondUserId = repository.create(TestUserObjects.newUser2());

        Assertions.assertThat(userStateOfId(firstUserId))
                .isEqualTo(UserState.CREATED);
        Assertions.assertThat(userStateOfId(secondUserId))
                .isEqualTo(UserState.CREATED);

        repository.updateState(firstUserId, UserState.ACTIVATED);

        Assertions.assertThat(userStateOfId(firstUserId))
                .isEqualTo(UserState.ACTIVATED);
        Assertions.assertThat(userStateOfId(secondUserId))
                .isEqualTo(UserState.CREATED);

    }

    private NewUser newUserOfId(UUID id) {
        return context.selectFrom(USER)
                .where(USER.ID.eq(id))
                .fetchOne(r -> new NewUser(r.getName(), r.getEmail(), r.getPassword()));
    }

    private UserState userStateOfId(UUID id) {
        return context.select(USER.STATE)
                .from(USER)
                .where(USER.ID.eq(id))
                .fetchOptional(USER.STATE)
                .map(UserState::valueOf)
                .orElseThrow();
    }

    public UUID createUser(User user) {
        context.newRecord(USER)
                .setId(user.id())
                .setName(user.name())
                .setEmail(user.email())
                .setPassword(user.password())
                .setState(user.state().name())
                .insert();

        return user.id();
    }
}
