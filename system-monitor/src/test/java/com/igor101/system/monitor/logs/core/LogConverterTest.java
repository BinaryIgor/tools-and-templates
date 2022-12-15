package com.igor101.system.monitor.logs.core;

import com.igor101.system.monitor.logs.core.model.*;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.stream.Stream;

public class LogConverterTest {

    private static final String APPLICATION_MESSAGE_TO_SWALLOW = "app-XXX";
    private static final String DEFAULT_MESSAGE_TO_SWALLOW = "XXX";
    private LogsConverter converter;

    @BeforeEach
    void setup() {
        var applicationLogMappings = List.of(
                new ApplicationLogMapping(List.of("postgres", "pos"),
                        new LogMapping(List.of("FATAL"),
                                List.of("PANIC"),
                                List.of(APPLICATION_MESSAGE_TO_SWALLOW))));

        converter = new LogsConverter(applicationLogMappings,
                new LogMapping(List.of("WARN", "warning"),
                        List.of("ERROR", "error"),
                        List.of(DEFAULT_MESSAGE_TO_SWALLOW)));
    }

    @ParameterizedTest
    @MethodSource("testCases")
    void shouldConvertLogsBasedOnConfigureMappings(LogData toConvert,
                                                   LogRecord converted) {
        Assertions.assertThat(converter.converted(toConvert))
                .isEqualTo(converted);
    }

    static Stream<Arguments> testCases() {
        var appInfo = new LogData("some-machine", "some-postgres", "some-postgres-1", "ordinary log, almost fatal");
        var appWarning = new LogData("some-another-machine", "some-pos", "II", "FATAL failure");
        var appError = new LogData("some-machine-3", "postgres", "33", "PANIC [run!!]");
        var appErrorToSwallow = new LogData("machine", "post", "999",
                "%s PANIC, but operating as usual!".formatted(APPLICATION_MESSAGE_TO_SWALLOW));

        return Stream.of(
                Arguments.of(appInfo, toExpectedLogRecord(appInfo, ApplicationLogLevel.INFO)),
                Arguments.of(appWarning, toExpectedLogRecord(appWarning, ApplicationLogLevel.WARNING)),
                Arguments.of(appError, toExpectedLogRecord(appError, ApplicationLogLevel.ERROR)),
                Arguments.of(appErrorToSwallow, toExpectedLogRecord(appErrorToSwallow, ApplicationLogLevel.INFO)));
    }

    private static LogRecord toExpectedLogRecord(LogData data, ApplicationLogLevel level) {
        return new LogRecord(data.source(), data.application(), data.instanceId(), level, data.log());
    }
}
