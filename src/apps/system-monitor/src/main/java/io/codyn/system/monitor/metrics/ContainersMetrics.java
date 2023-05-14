package io.codyn.system.monitor.metrics;

import java.util.List;

public record ContainersMetrics(String machine,
                                List<ContainerMetrics> metrics) {
}
