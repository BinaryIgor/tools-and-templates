package io.codyn.app.template.user.infra;

import io.codyn.app.template._shared.domain.model.UserRole;
import io.codyn.app.template._shared.domain.model.UserRoles;
import io.codyn.app.template._shared.domain.model.UserState;
import io.codyn.app.template.user.domain.model.User;
import io.codyn.app.template.user.domain.model.auth.NewUser;
import io.codyn.app.template.user.domain.model.auth.ToSignInUser;
import io.codyn.app.template.user.test.TestUserObjects;
import io.codyn.sqldb.test.DbIntegrationTest;
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
        var newUser = TestUserObjects.newUser();

        var id = repository.create(newUser);

        Assertions.assertThat(newUserOfId(id))
                .isEqualTo(newUser);
    }

    @Test
    void shouldFindUserByEmail() {
        var users = TestUserObjects.users();

        users.forEach(this::insertUser);

        users.forEach(u -> {
            Assertions.assertThat(repository.ofEmail(u.email()))
                    .get()
                    .isEqualTo(u);
        });
    }

    @Test
    void shouldUpdateUserState() {
        var userId1 = repository.create(TestUserObjects.newUser1());
        var userId2 = repository.create(TestUserObjects.newUser2());

        Assertions.assertThat(userStateOfId(userId1))
                .isEqualTo(UserState.CREATED);
        Assertions.assertThat(userStateOfId(userId2))
                .isEqualTo(UserState.CREATED);

        repository.updateState(userId1, UserState.ACTIVATED);

        Assertions.assertThat(userStateOfId(userId1))
                .isEqualTo(UserState.ACTIVATED);
        Assertions.assertThat(userStateOfId(userId2))
                .isEqualTo(UserState.CREATED);
    }

    @Test
    void shouldReturnToSignUserData() {
        var user1 = TestUserObjects.user1();
        var user1SecondFactor = true;
        var user2 = TestUserObjects.user2();
        var user2SecondFactor = false;

        insertUser(user1, user1SecondFactor);
        insertUser(user2, user2SecondFactor);

        var user1Roles = Set.of(UserRole.ADMIN, UserRole.MODERATOR);
        insertUserRoles(user1.id(), user1Roles);

        var expectedToSignInUser1 = toSignInUser(user1, user1SecondFactor, user1Roles);
        var expectedToSignInUser2 = toSignInUser(user2, user2SecondFactor, Set.of());

        Assertions.assertThat(repository.toSignInUserOfEmail(user1.email()))
                .get()
                .isEqualTo(expectedToSignInUser1);

        Assertions.assertThat(repository.toSignInUserOfEmail(user2.email()))
                .get()
                .isEqualTo(expectedToSignInUser2);
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

    public UUID insertUser(User user, boolean secondFactorAuthentication) {
        context.newRecord(USER)
                .setId(user.id())
                .setName(user.name())
                .setEmail(user.email())
                .setPassword(user.password())
                .setState(user.state().name())
                .setSecondFactorAuthentication(secondFactorAuthentication)
                .insert();

        return user.id();
    }

    public UUID insertUser(User user) {
        return insertUser(user, false);
    }

    public void insertUserRoles(UUID id, Collection<UserRole> roles) {
        roles.forEach(r -> context.insertInto(ROLE)
                .set(ROLE.ID, id)
                .set(ROLE.VALUE, r.name())
                .execute());
    }

    private ToSignInUser toSignInUser(User user, boolean secondFactorAuthentication, Collection<UserRole> roles) {
        return new ToSignInUser(user.id(), user.name(), user.email(), user.state(), user.password(),
                secondFactorAuthentication, UserRoles.of(roles));
    }
}
