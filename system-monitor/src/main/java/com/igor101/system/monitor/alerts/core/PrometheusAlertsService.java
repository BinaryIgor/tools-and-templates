package com.igor101.system.monitor.alerts.core;

import com.igor101.system.monitor.alerts.application.PrometheusAlerts;
import com.igor101.system.monitor.common.Metrics;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tag;
import io.micrometer.core.instrument.Tags;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class PrometheusAlertsService {

    private static final String ALERTS = Metrics.fullName("alerts_total");
    private final MeterRegistry meterRegistry;

    public PrometheusAlertsService(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
    }

    public void add(PrometheusAlerts alerts) {
        for (var a : alerts.alerts()) {

            var labels = new ArrayList<Tag>();
            labels.add(Tag.of("status", a.status()));
            a.labels().forEach((k, v) -> labels.add(Tag.of("label_%s".formatted(k), v)));
            a.annotations().forEach((k, v) -> labels.add(Tag.of("annotation_%s".formatted(k), v)));

            meterRegistry.counter(ALERTS, Tags.of(labels))
                    .increment();
        }
    }
}
