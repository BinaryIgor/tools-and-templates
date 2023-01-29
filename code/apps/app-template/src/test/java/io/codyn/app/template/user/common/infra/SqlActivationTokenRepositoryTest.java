package io.codyn.app.template.user.common.infra;

import io.codyn.app.template.user.auth.infra.SqlUserRepository;
import io.codyn.app.template.user.common.core.model.ActivationToken;
import io.codyn.app.template.user.common.core.model.ActivationTokenId;
import io.codyn.app.template.user.common.core.model.ActivationTokenType;
import io.codyn.app.template.user.common.test.TestUserObjects;
import io.codyn.sqldb.test.DbIntegrationTest;
import io.codyn.test.TestRandom;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

public class SqlActivationTokenRepositoryTest extends DbIntegrationTest {

    private SqlActivationTokenRepository repository;
    private SqlUserRepository userRepository;

    @Override
    protected void setup() {
        repository = new SqlActivationTokenRepository(contextProvider);
        userRepository = new SqlUserRepository(contextProvider);
    }

    @Test
    void shouldSaveAndDeleteActivationToken() {
        var user1Id = userRepository.create(TestUserObjects.user1());
        var user2Id = userRepository.create(TestUserObjects.user2());

        var activationToken1 = TestUserObjects.activationToken(user1Id, ActivationTokenType.NEW_USER);
        var activationToken2 = TestUserObjects.activationToken(user1Id, ActivationTokenType.NEW_EMAIL);
        var activationToken3 = TestUserObjects.activationToken(user2Id);

        repository.save(activationToken1);
        repository.save(activationToken2);
        repository.save(activationToken3);

        assertActivationTokenEquals(activationToken1);

        var updatedActivationToken1 = new ActivationToken(activationToken1.userId(), activationToken1.type(),
                TestRandom.string(), TestRandom.instant());

        repository.save(updatedActivationToken1);

        assertActivationTokenEquals(updatedActivationToken1);
        assertActivationTokenEquals(activationToken2);
        assertActivationTokenEquals(activationToken3);

        repository.delete(activationTokenId(activationToken1));

        assertActivationTokenDoesNotExist(activationToken1);
        assertActivationTokenExists(activationToken2);
        assertActivationTokenExists(activationToken3);
    }

    private void assertActivationTokenEquals(ActivationToken activationToken) {
        Assertions.assertThat(repository.ofId(activationTokenId(activationToken)))
                .get()
                .isEqualTo(activationToken);
    }

    private void assertActivationTokenExists(ActivationToken activationToken) {
        Assertions.assertThat(repository.ofId(activationTokenId(activationToken))).isPresent();
    }

    private void assertActivationTokenDoesNotExist(ActivationToken activationToken) {
        Assertions.assertThat(repository.ofId(activationTokenId(activationToken))).isEmpty();
    }

    private ActivationTokenId activationTokenId(ActivationToken activationToken) {
        return new ActivationTokenId(activationToken.userId(), activationToken.type());
    }
}
