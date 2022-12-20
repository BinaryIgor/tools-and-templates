package io.codyn.system.monitor.logs.app;

import io.codyn.system.monitor.IntegrationTest;
import io.codyn.system.monitor.logs.infra.FileLogsRepository;
import io.codyn.system.monitor.test.TestHttp;
import io.codyn.system.monitor.test.TestMetric;
import io.codyn.system.monitor.test.TestMetrics;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.actuate.observability.AutoConfigureObservability;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.springframework.http.HttpStatus;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@AutoConfigureObservability
@Import(LogsControllerTest.TestConfig.class)
public class LogsControllerTest extends IntegrationTest {

    private static final Clock FIXED_CLOCK = Clock.fixed(Instant.parse("2022-12-22T19:11:22Z"), ZoneId.of("UTC"));

    @Autowired
    private TestHttp testHttp;

    @Test
    void shouldAddLogsAndUpdatePrometheusMetrics() {
        var testCase = prepareAddLogsTestCase();

        testHttp.postAndExpectStatus("/logs", testCase.logsToSend, HttpStatus.OK);

        testHttp.getAndExpectOkStatusAndBody("/actuator/prometheus", String.class,
                txtMetrics -> {
                    var actualMetrics = TestMetrics.parseMetrics(txtMetrics);
                    Assertions.assertThat(actualMetrics)
                            .containsAll(testCase.expectedMetrics);
                });

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
        return String.valueOf(FIXED_CLOCK.instant().toEpochMilli() / 1000.0);
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

    @TestConfiguration
    static class TestConfig {
        @Bean
        @Primary
        Clock fixedClock() {
            return FIXED_CLOCK;
        }
    }
}
