package com.igor101.system.monitor.alerts.core;

import com.igor101.system.monitor.alerts.application.Alert;
import com.igor101.system.monitor.common.Metrics;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tag;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class AlertsService {

    static final String DESCRIPTION = "description";
    static final String ALERTS = Metrics.fullName("alerts_total");
    private final MeterRegistry meterRegistry;

    public AlertsService(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
    }

    public void add(List<Alert> alerts) {
        for (var a : alerts) {

            var tags = new ArrayList<Tag>();
            tags.add(Tag.of("status", a.status()));
            tags.addAll(prepareLabelsTags(a));
            tags.addAll(prepareAnnotationsTags(a));


            meterRegistry.counter(ALERTS, tags)
                    .increment();
        }
    }

    private List<Tag> prepareLabelsTags(Alert alert) {
        return prepareTags("label", alert.labels());
    }

    private List<Tag> prepareAnnotationsTags(Alert alert) {
        return prepareTags("annotation", alert.annotations());
    }

    private List<Tag> prepareTags(String prefix, Map<String, String> data) {
        return data.entrySet().stream()
                .filter(e -> !e.getKey().contains(DESCRIPTION))
                .map(e -> Tag.of("%s_%s".formatted(prefix, e.getKey()), e.getValue()))
                .toList();
    }
}
