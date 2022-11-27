package com.igor101.system.monitor.metrics.infrastructure;

import com.igor101.system.monitor.common.Metrics;
import com.igor101.system.monitor.metrics.application.ContainersMetrics;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tag;
import io.micrometer.core.instrument.Tags;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.ToDoubleFunction;
import java.util.stream.Collectors;

@Service
public class MetricsService {

    private static final String COLLECTOR_UP_TIMESTAMP = Metrics.fullName("collector_up_timestamp_seconds");
    private static final String APPLICATION_UP_TIMESTAMP = Metrics.fullName("application_up_timestamp_seconds");
    private static final String APPLICATION_USED_MEMORY = Metrics.fullName("application_used_memory_bytes");
    private static final String APPLICATION_MAX_MEMORY = Metrics.fullName("application_max_memory_bytes");
    private static final String APPLICATION_CPU_USAGE = Metrics.fullName("application_cpu_usage_percent");
    private final Map<String, AtomicReference<Double>> metricsReferences = new ConcurrentHashMap<>();
    private final MeterRegistry meterRegistry;

    public MetricsService(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
    }

    public void add(ContainersMetrics containersMetrics) {
        for (var m : containersMetrics.metrics()) {
            var tags = Tags.of(Metrics.applicationLabels(containersMetrics.source(),
                    m.containerName(), m.instanceId()));

            var secondsTimestamp = m.timestamp() / 1000.0;

            setMetricValue(COLLECTOR_UP_TIMESTAMP, Tags.of(Metrics.SOURCE_LABEL, containersMetrics.source()),
                    secondsTimestamp);

            setMetricValue(APPLICATION_UP_TIMESTAMP, tags, secondsTimestamp);
            setMetricValue(APPLICATION_USED_MEMORY, tags, m.usedMemory());
            setMetricValue(APPLICATION_MAX_MEMORY, tags, m.maxMemory());
            setMetricValue(APPLICATION_CPU_USAGE, tags, m.cpuUsage());
        }
    }

    private void setMetricValue(String metric, Tags tags, double value) {
        var previousReference = metricsReferences.computeIfAbsent(metricReferenceId(metric, tags),
                k -> new AtomicReference<>());
        previousReference.set(value);

        meterRegistry.gauge(metric, tags, previousReference, AtomicReference::get);
    }

    private String metricReferenceId(String metric, Tags tags) {
        return metric + "_" + tags.stream()
                .map(Tag::getValue)
                .collect(Collectors.joining());
    }
}
