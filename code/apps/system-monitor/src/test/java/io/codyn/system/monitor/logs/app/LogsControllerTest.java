package io.codyn.system.monitor.logs.app;

import io.codyn.system.monitor.IntegrationTest;
import io.codyn.system.monitor.logs.infra.FileLogsRepository;
import io.codyn.system.monitor.test.TestMetric;
import io.codyn.system.monitor.test.TestMetrics;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.actuate.observability.AutoConfigureObservability;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@AutoConfigureObservability
public class LogsControllerTest extends IntegrationTest {

    @BeforeEach
    void setup() {
        clock.setTime(Instant.parse("2022-12-22T19:11:22Z"));
    }

    @Test
    void shouldAddLogsAndUpdatePrometheusMetrics() {
        var testCase = prepareAddLogsTestCase();

        testHttpClient.test()
                .path("/logs")
                .POST()
                .body(testCase.logsToSend)
                .execute();

        var txtMetrics = testHttpClient.test()
                .path("/actuator/prometheus")
                .GET()
                .execute();

        var actualMetrics = TestMetrics.parseMetrics(txtMetrics);
        Assertions.assertThat(actualMetrics)
                .containsAll(testCase.expectedMetrics);

        testCase.expectedLogFiles()
                .forEach(p -> Assertions.assertThat(Files.exists(p))
                        .withFailMessage(() -> "%s file doesn't exist!".formatted(p))
                        .isTrue());
    }

    private AddLogsTestCase prepareAddLogsTestCase() {
        var logs = List.of(new LogEntry("anonymous", "some-container", "some-log ERROR"),
                new LogEntry("anonymous", "some-container", "error next"),
                new LogEntry("anonymous-x", "some-container-nginx", "[error] some message"),
                new LogEntry("anonymous", "some-container", "some-log info"),
                new LogEntry("anonymousII", "some-containerII", "instance-3", "some-log WARNING"),
                new LogEntry("anonymousII", "some-containerII", "instance-3", "some-log warn"));

        var expectedMetrics = List.of(
                TestMetrics.metric("monitoring_application_logs_errors_total", "3.0"),
                TestMetrics.metric("monitoring_application_logs_warnings_total", "2.0"),
                TestMetrics.metric("monitoring_application_logs_error_timestamp_seconds",
                        Map.of("application", "some-container", "instance_id", "some-container-default",
                                "source", "anonymous"),
                        toSecondsTimestampString()),
                TestMetrics.metric("monitoring_application_logs_error_timestamp_seconds",
                        Map.of("application", "some-container-nginx",
                                "instance_id", "some-container-nginx-default",
                                "source", "anonymous-x"),
                        toSecondsTimestampString()),
                TestMetrics.metric("monitoring_application_logs_warning_timestamp_seconds",
                        Map.of("application", "some-containerII",
                                "instance_id", "instance-3", "source", "anonymousII"),
                        toSecondsTimestampString()));

        var logsToSend = logs.stream().map(LogEntry::toJson).toList();

        var expectedLogFiles = logs.stream()
                .map(l -> FileLogsRepository.absoluteLogFilePath(logsRoot,
                        l.host,
                        l.containerName,
                        l.instanceId == null ? ApiLogsMapper.defaultInstanceId(l.containerName) : l.instanceId))
                .toList();

        return new AddLogsTestCase(logsToSend, expectedMetrics, expectedLogFiles);
    }

    private String toSecondsTimestampString() {
        return String.valueOf(clock.instant().toEpochMilli() / 1000.0);
    }

    private record LogEntry(String host,
                            String containerName,
                            String instanceId,
                            String log) {

        LogEntry(String host, String containerName, String log) {
            this(host, containerName, null, log);
        }

        Map<String, String> toJson() {
            var entry = new HashMap<>(Map.of(ApiLogsMapper.HOST, host,
                    ApiLogsMapper.CONTAINER_NAME, containerName,
                    ApiLogsMapper.LOG, log));

            if (instanceId != null) {
                entry.put(ApiLogsMapper.INSTANCE_ID, instanceId);
            }

            return entry;
        }
    }

    private record AddLogsTestCase(List<Map<String, String>> logsToSend,
                                   List<TestMetric> expectedMetrics,
                                   List<Path> expectedLogFiles) {
    }
}
