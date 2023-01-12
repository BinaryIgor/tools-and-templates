package io.codyn.system.monitor.logs.infra;

import io.codyn.test.TestRandom;
import io.codyn.system.monitor.logs.domain.model.ApplicationLogLevel;
import io.codyn.system.monitor.logs.domain.model.LogRecord;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public class FileLogsRepositoryTest {

    private static final Clock FIXED_CLOCK = Clock.fixed(Instant.parse("2022-12-12T20:11:22Z"), ZoneId.of("UTC"));
    private static final String FIXED_DATE_TIME = "20221212-201122";
    @TempDir
    File root;
    private FileLogsRepository repository;
    private File logsRoot;
    private int maxFileSize;

    @BeforeEach
    void setup() {
        logsRoot = new File(root, "logs");
        maxFileSize = TestRandom.inRange(100, 1000);
        repository = new FileLogsRepository(FIXED_CLOCK, logsRoot.getAbsolutePath(), maxFileSize, 10);
    }

    @Test
    void shouldStoreNewLogsRotatingTooLargeFiles() {
        var testCase = prepareStoreTestCase();

        repository.store(testCase.firstLogs());
        repository.store(testCase.secondLogs());

        testCase.contentsOfLogsFiles()
                .forEach((f, c) -> {
                    Assertions.assertThat(fileContent(f))
                            .isEqualTo(c);
                });
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "a", "some-application_233", "machine_application.log"})
    void shouldThrowExceptionWhileExtractingInvalidLogFilename(String logFilename) {
        Assertions.assertThatThrownBy(() -> FileLogsRepository.extractedLogFilename(logFilename))
                .isInstanceOf(RuntimeException.class);
    }

    @ParameterizedTest
    @MethodSource("extractedLogFilenameTestCases")
    void shouldReturnExtractedLogFilename(String logFilename,
                                          String extractedLogFilename) {
        Assertions.assertThat(FileLogsRepository.extractedLogFilename(logFilename))
                .isEqualTo(extractedLogFilename);
    }

    private StoreTestCase prepareStoreTestCase() {
        var firstLogs = new ArrayList<LogRecord>();
        var secondLogs = new ArrayList<LogRecord>();
        var expectedContentsOfLogsFiles = new HashMap<String, String>();

        var firstLogGroupRecord = new LogRecord("source",
                "application", "app-default",
                ApplicationLogLevel.ERROR, "Some error log");
        var secondLogGroupRecord = logRecordWithDifferentLog(firstLogGroupRecord, "Some short log");

        firstLogs.add(firstLogGroupRecord);
        firstLogs.add(secondLogGroupRecord);
        expectedContentsOfLogsFiles.put(logFilePath(firstLogGroupRecord),
                expectedLogFileContent(firstLogGroupRecord.log(), secondLogGroupRecord.log()));

        var secondLogRecord = new LogRecord("some-other-source",
                "app-II", "app-II-abcd",
                ApplicationLogLevel.WARNING, "Nothing actually");

        firstLogs.add(secondLogRecord);
        expectedContentsOfLogsFiles.put(logFilePath(secondLogRecord), expectedLogFileContent(secondLogRecord.log()));

        var firstToRotateGroupLogRecord = new LogRecord("last-source",
                "last-app", "last-app-instance-II",
                ApplicationLogLevel.INFO, "Short");
        var secondToRotateGroupLogRecord = logRecordWithDifferentLog(firstToRotateGroupLogRecord,
                TestRandom.string(maxFileSize - firstToRotateGroupLogRecord.log().length(),
                        maxFileSize));

        firstLogs.add(firstToRotateGroupLogRecord);

        secondLogs.add(secondToRotateGroupLogRecord);

        expectedContentsOfLogsFiles.put(logFilePath(firstToRotateGroupLogRecord, true),
                expectedLogFileContent(firstToRotateGroupLogRecord.log()));
        expectedContentsOfLogsFiles.put(logFilePath(firstToRotateGroupLogRecord),
                expectedLogFileContent(secondToRotateGroupLogRecord.log()));

        return new StoreTestCase(firstLogs, secondLogs,
                expectedContentsOfLogsFiles);
    }

    private LogRecord logRecordWithDifferentLog(LogRecord record, String log) {
        return new LogRecord(record.source(), record.application(), record.instanceId(), record.level(), log);
    }

    private String logFilePath(LogRecord record) {
        return logFilePath(record, false);
    }

    private String logFilePath(LogRecord record, boolean rotated) {
        var dir = new File(logsRoot, record.application());
        if (rotated) {
            return new File(dir, "%s__%s__%s.log".formatted(record.source(), record.instanceId(),
                    FIXED_DATE_TIME)).getAbsolutePath();
        }

        return new File(dir, "%s__%s.log".formatted(record.source(), record.instanceId())).getAbsolutePath();
    }

    private String expectedLogFileContent(String... logs) {
        return String.join("\n", logs) + "\n";
    }

    private String fileContent(String filePath) {
        try {
            return Files.readString(Path.of(filePath));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    static Stream<Arguments> extractedLogFilenameTestCases() {
        return Stream.of(
                Arguments.of("machine__instance.log", "machine__instance"),
                Arguments.of("machine__instance__%s.log".formatted(FIXED_DATE_TIME),
                        "machine__instance"),
                Arguments.of("machine__instance_22_sth.log", "machine__instance"));
    }

    private record StoreTestCase(List<LogRecord> firstLogs,
                                 List<LogRecord> secondLogs,
                                 Map<String, String> contentsOfLogsFiles) {
    }
}
