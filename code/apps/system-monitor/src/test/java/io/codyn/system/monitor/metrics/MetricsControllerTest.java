package io.codyn.system.monitor.metrics;

import io.codyn.system.monitor.IntegrationTest;
import io.codyn.system.monitor.common.Metrics;
import io.codyn.system.monitor.test.TestMetric;
import io.codyn.system.monitor.test.TestMetrics;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.actuate.observability.AutoConfigureObservability;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@AutoConfigureObservability
public class MetricsControllerTest extends IntegrationTest {


    @BeforeEach
    void setup() {
        clock.setTime(Instant.parse("2022-12-24T22:11:22Z"));
    }

    @Test
    void shouldAddMetricsToPrometheus() {
        var testCase = prepareAddMetricsTestCase();

        testHttpClient.test()
                .path("/metrics")
                .POST()
                .body(testCase.metricsToSend)
                .execute()
                .expectStatusOk();

        var actualMetrics = testHttpClient.test()
                .path("/actuator/prometheus")
                .GET()
                .execute()
                .expectStatusOk()
                .expectBody(TestMetrics::parseMetrics);

        Assertions.assertThat(actualMetrics)
                .containsAll(testCase.expectedMetrics);
    }

    private AddMetricsTestCase prepareAddMetricsTestCase() {
        var now = clock.instant();

        var containersMetrics = List.of(
                new ContainerMetrics("nginx", Instant.parse("2022-10-10T10:00:00Z"), now,
                        100_000, 100_000_000, 0.2),
                new ContainerMetrics("java-app", Instant.parse("2022-12-10T13:00:00Z"), now.minusSeconds(1000),
                        250_000_000, 100_000_000_000L, 0.1),
                new ContainerMetrics("postgres", Instant.parse("2023-01-01T10:00:00Z"), now,
                        99_000_000, 500_000_000, 0.01));

        var machine = "some-machine";

        var expectedMetrics = new ArrayList<TestMetric>();

        expectedMetrics.add(TestMetrics.metric("monitoring_collector_up_timestamp_seconds", Map.of("machine", machine),
                toSecondsTimestampString(now)));

        containersMetrics.forEach(c -> expectedMetrics.addAll(toExpectedContainerMetrics(machine, c)));

        return new AddMetricsTestCase(new ContainersMetrics(machine, containersMetrics), expectedMetrics);
    }

    private String toSecondsTimestampString(Instant timestamp) {
        return String.valueOf(timestamp.toEpochMilli() / 1000.0);
    }

    private List<TestMetric> toExpectedContainerMetrics(String machine, ContainerMetrics metrics) {
        var containerLabels = Map.of(Metrics.MACHINE_LABEL, machine,
                Metrics.APPLICATION_LABEL, metrics.containerName());

        var expectedMetrics = new ArrayList<TestMetric>();

        expectedMetrics.add(TestMetrics.metric("monitoring_application_started_at_timestamp_seconds",
                containerLabels, toSecondsTimestampString(metrics.startedAt())));

        expectedMetrics.add(TestMetrics.metric("monitoring_application_up_timestamp_seconds",
                containerLabels, toSecondsTimestampString(metrics.timestamp())));

        expectedMetrics.add(TestMetrics.metric("monitoring_application_used_memory_bytes",
                containerLabels, String.valueOf((double) metrics.usedMemory())));

        expectedMetrics.add(TestMetrics.metric("monitoring_application_max_memory_bytes",
                containerLabels, String.valueOf((double) metrics.maxMemory())));

        expectedMetrics.add(TestMetrics.metric("monitoring_application_cpu_usage_percent",
                containerLabels, String.valueOf(metrics.cpuUsage())));

        return expectedMetrics;
    }


    private record AddMetricsTestCase(ContainersMetrics metricsToSend,
                                      List<TestMetric> expectedMetrics) {
    }
}
