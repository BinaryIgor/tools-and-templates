package io.codyn.system.monitor.alerts.domain;

import io.codyn.system.monitor._shared.Metrics;
import io.micrometer.core.instrument.Tag;
import io.micrometer.core.instrument.Tags;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AlertsServiceTest {

    private static final Clock FIXED_CLOCK = Clock.fixed(Instant.parse("2022-12-12T20:11:22Z"), ZoneId.of("UTC"));
    private AlertsService service;
    private SimpleMeterRegistry registry;

    @BeforeEach
    void setup() {
        registry = new SimpleMeterRegistry();
        service = new AlertsService(registry, FIXED_CLOCK);
    }

    @Test
    void shouldInitializeAlertsTotalCountersWithZeros() {
        Assertions.assertThat(registry.counter(AlertsService.ALERTS_TOTAL, Alerts.STATUS, Alerts.FIRING).count())
                .isZero();
        Assertions.assertThat(registry.counter(AlertsService.ALERTS_TOTAL, Alerts.STATUS, Alerts.RESOLVED).count())
                .isZero();
    }

    @Test
    void shouldAddAlertsToMetricsExcludingDescriptions() {
        var testCase = prepareAddTestCase();

        service.add(testCase.firstAddAlerts());
        service.add(testCase.secondAddAlerts());

        Assertions.assertThat(registry.counter(AlertsService.ALERTS_TOTAL, Alerts.STATUS, Alerts.FIRING).count())
                .isEqualTo(testCase.expectedFiringAlertsCounter);
        Assertions.assertThat(registry.counter(AlertsService.ALERTS_TOTAL, Alerts.STATUS, Alerts.RESOLVED).count())
                .isEqualTo(testCase.expectedResolvedAlertsCounter);

        testCase.expectedAlertsTimestampsValues()
                .forEach((t, m) -> {
                    var actualMetric = registry.get(AlertsService.ALERT_TIMESTAMP).tags(t).gauge().value();
                    Assertions.assertThat(actualMetric).isEqualTo(m);
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

            expectedCounterTagsValues.put(Tags.of(tags), Metrics.secondsTimestamp(FIXED_CLOCK));
        });

        return new AddTestCase(firstAddAlerts, secondAddAlerts,
                4, 1,
                expectedCounterTagsValues);
    }

    private List<Tag> mapToTags(String keyPrefix, Map<String, String> map) {
        return map.entrySet().stream()
                .filter(e -> !e.getKey().contains(AlertsService.DESCRIPTION))
                .map(e -> Tag.of("%s_%s".formatted(keyPrefix, e.getKey()), e.getValue()))
                .toList();
    }

    private record AddTestCase(List<Alert> firstAddAlerts,
                               List<Alert> secondAddAlerts,
                               double expectedFiringAlertsCounter,
                               double expectedResolvedAlertsCounter,
                               Map<Tags, Double> expectedAlertsTimestampsValues) {
    }
}
