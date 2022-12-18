package com.igor101.system.monitor.metrics.app;

import java.util.List;

public record ContainersMetrics(String source,
                                List<ContainerMetrics> metrics) {
}
