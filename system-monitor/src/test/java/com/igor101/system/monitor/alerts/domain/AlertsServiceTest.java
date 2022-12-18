package com.igor101.system.monitor.alerts.domain;

import com.igor101.system.monitor.alerts.app.Alert;
import io.micrometer.core.instrument.Tag;
import io.micrometer.core.instrument.Tags;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AlertsServiceTest {

    private AlertsService service;
    private SimpleMeterRegistry registry;

    @BeforeEach
    void setup() {
        registry = new SimpleMeterRegistry();
        service = new AlertsService(registry);
    }

    @Test
    void shouldAddAlertsToMetricsExcludingDescriptions() {
        var testCase = prepareAddTestCase();

        service.add(testCase.firstAddAlerts());
        service.add(testCase.secondAddAlerts());

        testCase.expectedCounterTagsValues()
                .forEach((t, c) -> {
                    Assertions.assertThat(registry.counter(AlertsService.ALERTS_TOTAL, t).count())
                            .isEqualTo(c);
                });
    }

    private AddTestCase prepareAddTestCase() {
        var duplicatedAlert = new Alert("firing",
                Map.of("job", "some-job",
                        "env", "dev"),
                Map.of("summary", "Bad things have happened",
                        "description", "Bad things details"));

        var firstAddAlerts = List.of(duplicatedAlert, duplicatedAlert,
                new Alert("resolved",
                        Map.of("job", "some_another_job",
                                "description", "Not going to end well"),
                        Map.of("summary", "Easy to fix issue")));

        var secondAddAlerts = List.of(duplicatedAlert,
                new Alert("firing", Map.of("env", "dev"),
                        Map.of("meta-annotation", "134")));

        var expectedCounterTagsValues = new HashMap<Tags, Double>();

        var alerts = new ArrayList<>(firstAddAlerts);
        alerts.addAll(secondAddAlerts);

        alerts.forEach(a -> {
            var tags = new ArrayList<Tag>();
            tags.add(Tag.of("status", a.status()));
            tags.addAll(mapToTags("label", a.labels()));
            tags.addAll(mapToTags("annotation", a.annotations()));

            expectedCounterTagsValues.merge(Tags.of(tags), 1.0, Double::sum);
        });

        return new AddTestCase(firstAddAlerts, secondAddAlerts, expectedCounterTagsValues);
    }

    private List<Tag> mapToTags(String keyPrefix, Map<String, String> map) {
        return map.entrySet().stream()
                .filter(e -> !e.getKey().contains(AlertsService.DESCRIPTION))
                .map(e -> Tag.of("%s_%s".formatted(keyPrefix, e.getKey()), e.getValue()))
                .toList();
    }

    private record AddTestCase(List<Alert> firstAddAlerts,
                               List<Alert> secondAddAlerts,
                               Map<Tags, Double> expectedCounterTagsValues) {
    }
}
