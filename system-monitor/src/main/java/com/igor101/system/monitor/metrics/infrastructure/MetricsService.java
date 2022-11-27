package com.igor101.system.monitor.metrics.infrastructure;

import com.igor101.system.monitor.metrics.application.ContainersMetrics;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tags;
import org.springframework.stereotype.Service;

@Service
public class MetricsService {

    static final String APPLICATION_LABEL = "monitored_application";
    static final String INSTANCE_ID_LABEL = "instance_id";
    static final String SOURCE_LABEL = "source";
    private static final String APPLICATION_UP_TIMESTAMP = "application_up_timestamp_seconds";
    private static final String APPLICATION_USED_MEMORY = "application_used_memory_bytes";
    private static final String APPLICATION_MAX_MEMORY = "application_max_memory_bytes";
    private static final String APPLICATION_CPU_USAGE = "application_cpu_usage_percent";
    private final MeterRegistry meterRegistry;

    public MetricsService(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
    }

    public void add(ContainersMetrics containersMetrics) {
        for (var m : containersMetrics.metrics()) {
            var tags = Tags.of(SOURCE_LABEL, containersMetrics.source(),
                    APPLICATION_LABEL, m.containerName(),
                    INSTANCE_ID_LABEL, m.instanceId());

            meterRegistry.gauge(APPLICATION_UP_TIMESTAMP, tags, m.timestamp() / 1000);
            meterRegistry.gauge(APPLICATION_USED_MEMORY, tags, m.usedMemory());
            meterRegistry.gauge(APPLICATION_MAX_MEMORY, tags, m.maxMemory());
            meterRegistry.gauge(APPLICATION_CPU_USAGE, tags, m.cpuUsage());
        }
    }
}
