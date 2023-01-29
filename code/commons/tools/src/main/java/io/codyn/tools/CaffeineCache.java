package io.codyn.tools;

import io.codyn.types.Cache;

import java.util.Optional;
import java.util.function.Supplier;

public class CaffeineCache<K, V> implements Cache<K, V> {

    private final com.github.benmanes.caffeine.cache.Cache<K, V> caffeineCache;

    public CaffeineCache(com.github.benmanes.caffeine.cache.Cache<K, V> caffeineCache) {
        this.caffeineCache = caffeineCache;
    }

    @Override
    public void put(K key, V value) {
        caffeineCache.put(key, value);
    }

    @Override
    public Optional<V> get(K key) {
        return Optional.ofNullable(caffeineCache.getIfPresent(key));
    }

    @Override
    public Optional<V> getCachingIfAbsent(K key, Supplier<Optional<V>> newValue) {
        var v = caffeineCache.get(key, k -> newValue.get().orElse(null));
        return Optional.ofNullable(v);
    }

    @Override
    public void evict(K key) {
        caffeineCache.invalidate(key);
    }
}
