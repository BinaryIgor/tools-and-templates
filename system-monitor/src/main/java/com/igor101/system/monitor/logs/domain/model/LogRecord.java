package com.igor101.system.monitor.logs.domain.model;

public record LogRecord(String source,
                        String application,
                        String instanceId,
                        ApplicationLogLevel level,
                        String log) {
}
