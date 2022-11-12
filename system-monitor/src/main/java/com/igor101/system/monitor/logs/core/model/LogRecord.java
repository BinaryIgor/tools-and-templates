package com.igor101.system.monitor.logs.core.model;

import java.time.Instant;

public record LogRecord(String source,
                        String application,
                        String instanceId,
                        ApplicationLogLevel level,
                        String log,
                        Instant receivedTimestamp,
                        Instant fromTimestamp,
                        Instant toTimestamp) {
}
