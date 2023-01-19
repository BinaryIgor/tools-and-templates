package io.codyn.system.monitor.metrics.app;

import java.time.Instant;

public record ContainerMetrics(String containerName,
                               String instanceId,
                               Instant startedAt,
                               Instant timestamp,
                               long usedMemory,
                               long maxMemory,
                               double cpuUsage) {
}
