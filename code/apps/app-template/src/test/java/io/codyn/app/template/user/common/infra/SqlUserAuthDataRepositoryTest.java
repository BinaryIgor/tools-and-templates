package io.codyn.app.template.user.common.infra;

import io.codyn.app.template._common.core.model.UserRole;
import io.codyn.app.template.auth.api.UserAuthData;
import io.codyn.app.template.user.common.test.TestUserClient;
import io.codyn.app.template.user.common.test.TestUserObjects;
import io.codyn.sqldb.test.DbIntegrationTest;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Set;

public class SqlUserAuthDataRepositoryTest extends DbIntegrationTest {

    private SqlUserAuthDataRepository repository;
    private TestUserClient userClient;

    @Override
    protected void setup() {
        repository = new SqlUserAuthDataRepository(contextProvider);
        userClient = new TestUserClient(contextProvider);
    }

    @Test
    void shouldReturnUserAuthData() {
        var user1 = TestUserObjects.user1();
        var user2 = TestUserObjects.user2();
        var user1Id = userClient.createUser(user1);
        var user2Id = userClient.createUser(user2);

        var user1Roles = Set.of(UserRole.ADMIN, UserRole.MODERATOR);
        userClient.setUserRoles(user1Id, user1Roles);

        var expectedUser1AuthData = new UserAuthData(user1Id, user1.state(), user1Roles);
        var expectedUser2AuthData = new UserAuthData(user2Id, user2.state(), Set.of());

        Assertions.assertThat(repository.ofId(user1Id))
                .get()
                .isEqualTo(expectedUser1AuthData);
        Assertions.assertThat(repository.ofId(user2Id))
                .get()
                .isEqualTo(expectedUser2AuthData);
    }
}
