package io.codyn.system.monitor.logs;

import io.codyn.system.monitor.logs.model.LogData;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class ApiLogsMapper {

    public static final String MACHINE_NAME = "machine_name";
    public static final String LOG = "log";
    public static final String CONTAINER_NAME = "container_name";
    static final String DEFAULT_NO_VALUE = "UNKNOWN";

    public static List<LogData> fromApiLogs(List<Map<String, String>> logs) {
        return logs.stream()
                .map(l -> {
                    var machine = l.getOrDefault(MACHINE_NAME, DEFAULT_NO_VALUE);
                    var application = l.getOrDefault(CONTAINER_NAME, DEFAULT_NO_VALUE).replaceFirst("/", "");
                    var log = l.get(LOG);

                    return log == null ? null : new LogData(machine, application, log);
                })
                .filter(Objects::nonNull)
                .toList();
    }

    public static Collection<String> logsHosts(List<Map<String, String>> logs) {
        return logs.stream().map(e -> e.getOrDefault(MACHINE_NAME, DEFAULT_NO_VALUE)).distinct().toList();
    }
}
