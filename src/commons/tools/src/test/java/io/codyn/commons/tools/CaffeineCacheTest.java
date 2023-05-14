package io.codyn.commons.tools;

import io.codyn.test.TestRandom;
import io.codyn.tools.CacheFactory;
import io.codyn.types.Cache;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Tag("integration")
public class CaffeineCacheTest {

    @Test
    void shouldCacheValue() {
        Cache<Integer, String> cache = CacheFactory.newCache(10);

        var key = 33;
        var value = TestRandom.string();

        Assertions.assertThat(cache.get(key)).isEmpty();

        cache.put(key, value);

        Assertions.assertThat(cache.get(key)).get().isEqualTo(value);
    }

    @Test
    void shouldCacheComputedPreviouslyValue() {
        Cache<Integer, String> cache = CacheFactory.newCache(10);

        var key = 99;
        var value = TestRandom.string();

        Assertions.assertThat(cache.get(key)).isEmpty();

        Assertions.assertThat(cache.getCachingIfAbsent(key, () -> Optional.of(value))).get().isEqualTo(value);

        cache.getCachingIfAbsent(key, Optional::empty);
        cache.getCachingIfAbsent(key, () -> Optional.of(TestRandom.string()));

        Assertions.assertThat(cache.get(key)).get().isEqualTo(value);
    }

    @Test
    void shouldEvictPreviousCacheValue() {
        Cache<String, String> cache = CacheFactory.newCache(10);

        var keys = List.of("first", "second", "third");

        keys.forEach(k -> cache.put(k, TestRandom.string()));

        var toEvictKey = TestRandom.oneOf(keys);

        cache.evict(toEvictKey);

        keys.forEach(k -> {
            if (k.equals(toEvictKey)) {
                Assertions.assertThat(cache.get(k)).isEmpty();
            } else {
                Assertions.assertThat(cache.get(k)).isPresent();
            }
        });
    }

    @Test
    void shouldRemoveValuesAfterExceedingMaxSize() throws Exception {
        var maxSize = TestRandom.inRange(100, 1000);
        Cache<Long, DataObject> cache = CacheFactory.newCache(maxSize);

        var keys = new ArrayList<Long>();

        var largerSizeToTriggerEviction = (int) (1.5 * maxSize);

        for (long i = 0; i < largerSizeToTriggerEviction; i++) {
            cache.put(i, new DataObject(i, TestRandom.string()));
            keys.add(i);
        }

        var sleepToTriggerEviction = TestRandom.inRange(500, 2000);
        Thread.sleep(sleepToTriggerEviction);

        var presentCount = keys.stream().filter(k -> cache.get(k).isPresent()).count();

        Assertions.assertThat(presentCount).isLessThanOrEqualTo(maxSize);
    }

    @Test
    void shouldRemoveValuesAfterExpiredTimeToLive() throws Exception {
        Cache<Long, String> cache = CacheFactory.newCache(100, 1L);

        var key = 44L;
        cache.put(key, "Value");
        Assertions.assertThat(cache.get(key)).isPresent();

        Thread.sleep(1500);

        Assertions.assertThat(cache.get(key)).isEmpty();
    }

    private record DataObject(long id, String name) {
    }
}
