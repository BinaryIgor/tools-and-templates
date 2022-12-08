package com.igor101.system.monitor.metrics.core;

import com.igor101.system.monitor.common.Metrics;
import com.igor101.system.monitor.metrics.application.ContainersMetrics;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tag;
import io.micrometer.core.instrument.Tags;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@Service
public class MetricsService {

    private static final String COLLECTOR_UP_TIMESTAMP = Metrics.fullName("collector_up_timestamp_seconds");
    private static final String APPLICATION_STARTED_AT_TIMESTAMP =
            Metrics.fullName("application_started_at_timestamp_seconds");
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
        setMetricValue(COLLECTOR_UP_TIMESTAMP, Tags.of(Metrics.SOURCE_LABEL, containersMetrics.source()),
                secondsTimestamp(System.currentTimeMillis()));

        for (var m : containersMetrics.metrics()) {
            var tags = Tags.of(Metrics.applicationLabels(containersMetrics.source(),
                    m.containerName(), m.instanceId()));

            setMetricValue(APPLICATION_STARTED_AT_TIMESTAMP, tags, secondsTimestamp(m.startedAt()));
            setMetricValue(APPLICATION_UP_TIMESTAMP, tags, secondsTimestamp(m.timestamp()));
            setMetricValue(APPLICATION_USED_MEMORY, tags, m.usedMemory());
            setMetricValue(APPLICATION_MAX_MEMORY, tags, m.maxMemory());
            setMetricValue(APPLICATION_CPU_USAGE, tags, m.cpuUsage());
        }
    }

    private static double secondsTimestamp(LocalDateTime dateTime) {
        return secondsTimestamp(dateTime.toInstant(ZoneOffset.UTC).toEpochMilli());
    }

    private static double secondsTimestamp(long timestamp) {
        return timestamp / 1000.0;
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
