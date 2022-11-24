package com.igor101.system.monitor.logs.application;

import com.igor101.system.monitor.logs.core.model.LogData;

import java.util.List;
import java.util.Map;
import java.util.Objects;

public class ApiLogsMapper {

    static final String DEFAULT_NO_VALUE = "UNKNOWN";
    static final String HOST = "host";
    static final String LOG = "log";
    static final String CONTAINER_NAME = "container_name";
    static final String INSTANCE_ID = "instance-id";

    public static List<LogData> fromApiLogs(List<Map<String, String>> logs) {
        return logs.stream()
                .map(l -> {
                    var source = l.getOrDefault(HOST, DEFAULT_NO_VALUE);
                    var application = l.getOrDefault(CONTAINER_NAME, DEFAULT_NO_VALUE).replaceFirst("/", "");
                    var instanceId = l.getOrDefault(INSTANCE_ID, application);
                    var log = l.get(LOG);

                    return log == null ? null : new LogData(source, application, instanceId, log);
                })
                .filter(Objects::nonNull)
                .toList();
    }
}
