package io.codyn.app.template.user.common.common;

import io.codyn.app.template._common.core.model.UserRole;
import io.codyn.app.template._common.core.model.UserState;
import io.codyn.app.template.auth.api.UserAuthData;
import io.codyn.app.template.user.common.core.UserStateChangedEvent;
import io.codyn.app.template.user.common.core.cache.CacheableUserAuthDataRepository;
import io.codyn.app.template.user.common.test.TestUserAuthDataRepository;
import io.codyn.tools.CacheFactory;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;
import java.util.UUID;

public class CacheableUserAuthDataRepositoryTest {

    private TestUserAuthDataRepository baseRepository;

    private CacheableUserAuthDataRepository repository;
    private boolean isCacheEnabled;

    @BeforeEach
    void setup() {
        baseRepository = new TestUserAuthDataRepository();

        isCacheEnabled = true;

        repository = new CacheableUserAuthDataRepository(baseRepository,
                CacheFactory.newCache(99),
                () -> isCacheEnabled);
    }

    @Test
    void shouldNotUseCacheWhenDisabled() {
        isCacheEnabled = false;

        var userId = UUID.randomUUID();
        var userAuthData = new UserAuthData(userId, UserState.ACTIVATED, Set.of(UserRole.ADMIN));

        baseRepository.addUserData(userAuthData);

        assertHasUserAuthDataInCache(userAuthData);

        var changedUserAuthData = new UserAuthData(userId, UserState.CREATED, Set.of());

        baseRepository.addUserData(changedUserAuthData);

        assertHasUserAuthDataInCache(changedUserAuthData);
    }

    private void assertHasUserAuthDataInCache(UserAuthData userAuthData) {
        Assertions.assertThat(repository.ofId(userAuthData.id()))
                .get()
                .isEqualTo(userAuthData);
    }

    @Test
    void shouldUseCacheWhenEnabled() {
        var userId = UUID.randomUUID();
        var userAuthData = new UserAuthData(userId, UserState.ONBOARDED, Set.of());

        baseRepository.addUserData(userAuthData);

        assertHasUserAuthDataInCache(userAuthData);

        baseRepository.removeUserData(userId);

        assertHasUserAuthDataInCache(userAuthData);
    }

    @Test
    void shouldEvictCacheOnUserStateChangedEvent() {
        var user1Id = UUID.randomUUID();
        var user2Id = UUID.randomUUID();
        var user1AuthData = new UserAuthData(user1Id, UserState.ACTIVATED, Set.of(UserRole.ADMIN));
        var user2AuthData = new UserAuthData(user2Id, UserState.CREATED, Set.of());

        baseRepository.addUserData(user1AuthData);
        baseRepository.addUserData(user2AuthData);

        assertHasUserAuthDataInCache(user1AuthData);
        assertHasUserAuthDataInCache(user2AuthData);

        var changedUser1AuthData = new UserAuthData(user1Id, UserState.CREATED, Set.of());
        baseRepository.addUserData(changedUser1AuthData);

        repository.handle(new UserStateChangedEvent(user1Id, UserState.ACTIVATED));

        assertHasUserAuthDataInCache(changedUser1AuthData);
        assertHasUserAuthDataInCache(user2AuthData);
    }
}
