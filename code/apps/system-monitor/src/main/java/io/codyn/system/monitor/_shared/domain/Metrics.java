package io.codyn.system.monitor._shared.domain;

import java.time.Instant;

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

    public static double secondsTimestamp(Instant timestamp) {
        return secondsTimestamp(timestamp.toEpochMilli());
    }

    public static double secondsTimestamp(long timestamp) {
        return timestamp / 1000.0;
    }

}
