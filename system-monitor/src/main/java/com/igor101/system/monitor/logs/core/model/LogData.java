package com.igor101.system.monitor.logs.core.model;

public record LogData(String source,
                      String application,
                      String instanceId,
                      String log) {
}
