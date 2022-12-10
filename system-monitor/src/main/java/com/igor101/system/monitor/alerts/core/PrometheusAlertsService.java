package com.igor101.system.monitor.alerts.core;

import com.igor101.system.monitor.alerts.application.PrometheusAlert;
import com.igor101.system.monitor.alerts.application.PrometheusAlerts;
import com.igor101.system.monitor.common.Metrics;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tag;
import io.micrometer.core.instrument.Tags;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class PrometheusAlertsService {

    static final String DESCRIPTION = "description";
    private static final String ALERTS = Metrics.fullName("alerts_total");
    private final MeterRegistry meterRegistry;

    public PrometheusAlertsService(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
    }

    public void add(PrometheusAlerts alerts) {
        for (var a : alerts.alerts()) {

            var tags = new ArrayList<Tag>();
            tags.add(Tag.of("status", a.status()));
            tags.addAll(preparedLabelsTags(a));
            tags.addAll(preparedAnnotationsTags(a));

            meterRegistry.counter(ALERTS, Tags.of(tags))
                    .increment();
        }
    }

    private List<Tag> preparedLabelsTags(PrometheusAlert alert) {
        return alert.labels().entrySet().stream()
                .filter(e -> !e.getKey().contains(DESCRIPTION))
                .map(e -> Tag.of("label_%s".formatted(e.getKey()), e.getValue()))
                .toList();
    }

    private List<Tag> preparedAnnotationsTags(PrometheusAlert alert) {
        return alert.annotations().entrySet().stream()
                .filter(e -> !e.getKey().contains(DESCRIPTION))
                .map(e -> Tag.of("annotation_%s".formatted(e.getKey()), e.getValue()))
                .toList();
    }
}
