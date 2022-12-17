package com.igor101.system.monitor.logs.application;

import com.igor101.system.monitor.logs.core.model.LogData;

import java.util.*;

public class ApiLogsMapper {

    public static final String HOST = "host";
    public static final String LOG = "log";
    public static final String CONTAINER_NAME = "container_name";
    public static final String INSTANCE_ID = "instance-id";
    static final String DEFAULT_NO_VALUE = "UNKNOWN";

    public static List<LogData> fromApiLogs(List<Map<String, String>> logs) {
        return logs.stream()
                .map(l -> {
                    var source = l.getOrDefault(HOST, DEFAULT_NO_VALUE);
                    var application = l.getOrDefault(CONTAINER_NAME, DEFAULT_NO_VALUE).replaceFirst("/", "");
                    var instanceId = l.getOrDefault(INSTANCE_ID, defaultInstanceId(application));
                    var log = l.get(LOG);

                    return log == null ? null : new LogData(source, application, instanceId, log);
                })
                .filter(Objects::nonNull)
                .toList();
    }

    public static String defaultInstanceId(String containerName) {
        return containerName + "-default";
    }

    public static Collection<String> logsHosts(List<Map<String, String>> logs) {
        return logs.stream().map(e -> e.getOrDefault(HOST, DEFAULT_NO_VALUE)).distinct().toList();
    }
}
