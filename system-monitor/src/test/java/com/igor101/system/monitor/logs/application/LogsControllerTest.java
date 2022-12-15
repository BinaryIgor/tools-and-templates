package com.igor101.system.monitor.logs.application;

import com.igor101.system.monitor.IntegrationTest;
import com.igor101.system.monitor.logs.infrastructure.FileLogsRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.actuate.metrics.AutoConfigureMetrics;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@AutoConfigureMetrics
public class LogsControllerTest extends IntegrationTest {

    @Autowired
    TestRestTemplate testRestTemplate;

    @Test
    void shouldAddLogsAndUpdatePrometheusMetrics() {
        var testCase = prepareAddLogsTestCase();

        Assertions.assertThat(testRestTemplate.postForEntity("/logs", testCase.logsToSend(), null).getStatusCode())
                .isEqualTo(HttpStatus.OK);

        var actualMetrics = testRestTemplate.getForObject("/actuator/prometheus", String.class);
        Assertions.assertThat(actualMetrics)
                .contains(testCase.expectedMetrics());

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

        var expectedMetrics = List.of("""
                monitoring_application_logs_errors_total{application="some-container",instance_id="some-container-default",source="anonymous",} 2.0
                """.trim(), """
                monitoring_application_logs_errors_total{application="some-container-nginx",instance_id="some-container-nginx-default",source="anonymous-x",} 1.0
                """.trim(), """
                monitoring_application_logs_warnings_total{application="some-containerII",instance_id="instance-3",source="anonymousII",} 2.0
                """.trim());

        var logsToSend = logs.stream().map(LogEntry::toJson).toList();

        var expectedLogFiles = logs.stream()
                .map(l -> FileLogsRepository.absoluteLogFilePath(logsRoot,
                        l.host,
                        l.containerName,
                        l.instanceId == null ? ApiLogsMapper.defaultInstanceId(l.containerName) : l.instanceId))
                .toList();

        return new AddLogsTestCase(logsToSend, expectedMetrics, expectedLogFiles);
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
                                   List<String> expectedMetrics,
                                   List<Path> expectedLogFiles) {
    }
}
