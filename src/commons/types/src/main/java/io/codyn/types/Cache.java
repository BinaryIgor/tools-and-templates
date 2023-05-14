package io.codyn.types;

import java.util.Optional;
import java.util.function.Supplier;

//TODO: pub-sub needed to use it properly (invalidation)!
public interface Cache<K, V> {

    void put(K key, V value);

    Optional<V> get(K key);

    Optional<V> getCachingIfAbsent(K key, Supplier<Optional<V>> newValue);

    void evict(K key);
}
