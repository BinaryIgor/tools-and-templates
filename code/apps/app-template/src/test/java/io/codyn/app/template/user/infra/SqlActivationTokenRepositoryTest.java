package io.codyn.app.template.user.infra;

import io.codyn.app.template.user.domain.model.activation.ActivationToken;
import io.codyn.app.template.user.domain.model.activation.ActivationTokenId;
import io.codyn.app.template.user.domain.model.activation.ActivationTokenType;
import io.codyn.app.template.user.test.TestUserObjects;
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
        var userId1 = userRepository.create(TestUserObjects.newUser1());
        var userId2 = userRepository.create(TestUserObjects.newUser2());

        var activationToken1 = TestUserObjects.activationToken(userId1, ActivationTokenType.NEW_USER);
        var activationToken2 = TestUserObjects.activationToken(userId1, ActivationTokenType.NEW_EMAIL);
        var activationToken3 = TestUserObjects.activationToken(userId2);

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
