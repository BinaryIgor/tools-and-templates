package io.codyn.system.monitor.metrics;

import java.time.Instant;

public record ContainerMetrics(String containerName,
                               Instant startedAt,
                               Instant timestamp,
                               long usedMemory,
                               long maxMemory,
                               double cpuUsage) {
}
