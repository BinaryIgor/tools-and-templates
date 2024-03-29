package io.codyn.app.template.user.auth.infra;

import io.codyn.app.template._common.core.model.UserRole;
import io.codyn.app.template._common.core.model.UserState;
import io.codyn.app.template.user.common.core.model.User;
import io.codyn.app.template.user.common.test.TestUserObjects;
import io.codyn.sqldb.test.DbIntegrationTest;
import io.codyn.test.TestRandom;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Collection;
import java.util.Set;
import java.util.UUID;

import static io.codyn.sqldb.schema.user.tables.Role.ROLE;
import static io.codyn.sqldb.schema.user.tables.User.USER;

public class SqlUserRepositoryTest extends DbIntegrationTest {

    private SqlUserRepository repository;

    @Override
    protected void setup() {
        repository = new SqlUserRepository(contextProvider);
    }

    @Test
    void shouldCreateUser() {
        var user = TestUserObjects.user();

        repository.create(user);

        Assertions.assertThat(userOfId(user.id()))
                .isEqualTo(user);
    }

    @Test
    void shouldFindUserByEmail() {
        var users = TestUserObjects.users();

        users.forEach(repository::create);

        users.forEach(u -> {
            Assertions.assertThat(repository.ofEmail(u.email()))
                    .get()
                    .isEqualTo(u);
        });
    }

    @Test
    void shouldUpdateUserState() {
        var user1 = TestUserObjects.user1();
        var user2 = TestUserObjects.user2();
        var user1Id = repository.create(user1);
        var user2Id = repository.create(user2);

        Assertions.assertThat(userStateOfId(user1Id))
                .isEqualTo(user1.state());
        Assertions.assertThat(userStateOfId(user2Id))
                .isEqualTo(user2.state());

        var user1NewState = TestRandom.oneOfExcluding(UserState.values(), user1.state());
        repository.updateState(user1Id, user1NewState);

        Assertions.assertThat(userStateOfId(user1Id))
                .isEqualTo(user1NewState);
        Assertions.assertThat(userStateOfId(user2Id))
                .isEqualTo(user2.state());
    }

    @Test
    void shouldUpdateUserPassword() {
        var user1 = TestUserObjects.user1();
        var user2 = TestUserObjects.user2();
        var user1Id = repository.create(user1);
        var user2Id = repository.create(user2);

        Assertions.assertThat(userPasswordOfId(user1Id))
                .isEqualTo(user1.password());
        Assertions.assertThat(userPasswordOfId(user2Id))
                .isEqualTo(user2.password());

        var user1NewPassword = user1.password() + "-234";
        repository.updatePassword(user1Id, user1NewPassword);

        Assertions.assertThat(userPasswordOfId(user1Id))
                .isEqualTo(user1NewPassword);
        Assertions.assertThat(userPasswordOfId(user2Id))
                .isEqualTo(user2.password());
    }

    @Test
    void shouldReturnRolesOfUser() {
        var user1Id = repository.create(TestUserObjects.user1());
        var user2Id = repository.create(TestUserObjects.user2());

        var user1Roles = Set.of(UserRole.ADMIN, UserRole.MODERATOR);
        insertUserRoles(user1Id, user1Roles);

        Assertions.assertThat(repository.rolesOfUser(user1Id))
                .containsExactlyElementsOf(user1Roles);

        Assertions.assertThat(repository.rolesOfUser(user2Id)).isEmpty();
    }

    private User userOfId(UUID id) {
        return context.selectFrom(USER)
                .where(USER.ID.eq(id))
                .fetchOne(r -> new User(r.getId(), r.getName(), r.getEmail(), r.getPassword(),
                        UserState.valueOf(r.getState()), r.getSecondFactorAuth()));
    }

    private UserState userStateOfId(UUID id) {
        return userOfId(id).state();
    }

    private String userPasswordOfId(UUID id) {
        return userOfId(id).password();
    }

    public void insertUserRoles(UUID id, Collection<UserRole> roles) {
        roles.forEach(r -> context.insertInto(ROLE)
                .set(ROLE.USER_ID, id)
                .set(ROLE.VALUE, r.name())
                .execute());
    }

}
