package com.igor101.system.monitor._shared;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

public class Metrics {
    public static final String APPLICATION_LABEL = "application";
    public static final String INSTANCE_ID_LABEL = "instance_id";
    public static final String SOURCE_LABEL = "source";

    public static String[] applicationLabels(String source, String application, String instanceId) {
        return new String[]{SOURCE_LABEL, source, APPLICATION_LABEL, application, INSTANCE_ID_LABEL, instanceId};
    }

    public static String fullName(String metric) {
        return "%s_%s".formatted("monitoring", metric);
    }

    public static double secondsTimestamp(LocalDateTime dateTime) {
        return secondsTimestamp(dateTime.toInstant(ZoneOffset.UTC).toEpochMilli());
    }

    public static double secondsTimestamp(Instant timestamp) {
        return secondsTimestamp(timestamp.toEpochMilli());
    }

    public static double secondsTimestamp(Clock clock) {
        return secondsTimestamp(clock.instant());
    }

    public static double secondsTimestamp(long timestamp) {
        return timestamp / 1000.0;
    }

}
