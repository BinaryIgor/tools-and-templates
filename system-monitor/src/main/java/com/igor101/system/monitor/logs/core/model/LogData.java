package com.igor101.system.monitor.logs.core.model;

public record LogData(String source,
                      String application,
                      String instanceId,
                      long from,
                      long to,
                      String log) {
}
