package com.igor101.system.monitor.common;

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
}
