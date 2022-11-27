package com.igor101.system.monitor.metrics.application;

public record ContainerMetrics(String containerName,
                               String instanceId,
                               long timestamp,
                               long usedMemory,
                               long maxMemory,
                               double cpuUsage) {
}
