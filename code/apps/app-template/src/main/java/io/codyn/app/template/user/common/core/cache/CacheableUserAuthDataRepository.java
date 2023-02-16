package io.codyn.app.template.user.common.core.cache;

import io.codyn.app.template.auth.api.UserAuthData;
import io.codyn.app.template.auth.api.UserAuthDataRepository;
import io.codyn.app.template.user.common.core.UserStateChangedEvent;
import io.codyn.types.Cache;
import io.codyn.types.EventHandler;

import java.util.Optional;
import java.util.UUID;
import java.util.function.Supplier;

public class CacheableUserAuthDataRepository implements UserAuthDataRepository,
        EventHandler<UserStateChangedEvent> {

    private final UserAuthDataRepository base;
    private final Cache<UUID, UserAuthData> cache;
    private final Supplier<Boolean> isCacheEnabled;

    public CacheableUserAuthDataRepository(UserAuthDataRepository base,
                                           Cache<UUID, UserAuthData> cache,
                                           Supplier<Boolean> isCacheEnabled) {
        this.base = base;
        this.cache = cache;
        this.isCacheEnabled = isCacheEnabled;
    }

    @Override
    public Optional<UserAuthData> ofId(UUID id) {
        if (isCacheEnabled.get()) {
            return cache.getCachingIfAbsent(id, () -> base.ofId(id));
        }
        return base.ofId(id);
    }

    @Override
    public void handle(UserStateChangedEvent event) {
        cache.evict(event.id());
    }
}
