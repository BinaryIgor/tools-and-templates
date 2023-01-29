package io.codyn.tools;

import com.github.benmanes.caffeine.cache.Caffeine;
import io.codyn.types.Cache;

import java.util.concurrent.TimeUnit;

public class CacheFactory {

    public static <K, V> Cache<K, V> newCache(int maxEntries, Long ttl) {
        //records stats, when needed
        var caffeine = Caffeine.newBuilder()
                .maximumSize(maxEntries);

        if (ttl != null) {
            caffeine.expireAfterWrite(ttl, TimeUnit.SECONDS);
        }

        return new CaffeineCache<>(caffeine.build());
    }

    public static <K, V> Cache<K, V> newCache(int maxEntries) {
        return newCache(maxEntries, null);
    }
}
