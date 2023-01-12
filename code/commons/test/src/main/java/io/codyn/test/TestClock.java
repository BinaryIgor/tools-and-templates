package io.codyn.test;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;

public class TestClock extends Clock {

    private Instant instant;

    public TestClock(Instant instant) {
        this.instant = instant;
    }

    public TestClock() {
        this(Instant.now());
    }

    @Override
    public ZoneId getZone() {
        return ZoneId.of("UTC");
    }

    @Override
    public Clock withZone(ZoneId zone) {
        return this;
    }

    @Override
    public Instant instant() {
        return instant;
    }

    public void setTime(Instant instant) {
        this.instant = instant;
    }

    public void moveForward(Duration duration) {
        instant = instant.plus(duration);
    }

    public void moveBack(Duration duration) {
        instant = instant.minus(duration);
    }

}
