package io.codyn.app.template._common.core;

import java.time.Duration;
import java.time.Instant;
import java.util.function.Supplier;

public class AfterDelayCacheEnabler implements Supplier<Boolean> {

    private final Duration delay;
    private final Instant startedAt;
    private boolean enabled;

    public AfterDelayCacheEnabler(Duration delay) {
        this.delay = delay;
        this.startedAt = Instant.now();
        this.enabled = false;
    }

    public AfterDelayCacheEnabler() {
        this(Duration.ofSeconds(30));
    }

    @Override
    public Boolean get() {
        if (enabled) {
            return true;
        }
        enabled = Duration.between(startedAt, Instant.now()).compareTo(delay) >= 0;
        return enabled;
    }
}
