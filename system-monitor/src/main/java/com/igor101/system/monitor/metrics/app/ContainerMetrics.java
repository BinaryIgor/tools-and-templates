package com.igor101.system.monitor.metrics.app;

import java.time.LocalDateTime;

public record ContainerMetrics(String containerName,
                               String instanceId,
                               LocalDateTime startedAt,
                               long timestamp,
                               long usedMemory,
                               long maxMemory,
                               double cpuUsage) {
}
