package io.codyn.system.monitor.logs.model;

public record LogRecord(String machine,
                        String application,
                        ApplicationLogLevel level,
                        String log) {
}
