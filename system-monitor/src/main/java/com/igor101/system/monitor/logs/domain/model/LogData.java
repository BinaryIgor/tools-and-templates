package com.igor101.system.monitor.logs.domain.model;

public record LogData(String source,
                      String application,
                      String instanceId,
                      String log) {
}
