package io.codyn.system.monitor.logs.domain;

import io.codyn.system.monitor._shared.Gauges;
import io.codyn.system.monitor._shared.Metrics;
import io.codyn.system.monitor.logs.domain.model.ApplicationLogLevel;
import io.codyn.system.monitor.logs.domain.model.LogData;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tags;

import java.time.Clock;
import java.util.List;

public class LogsService {

    static final String APPLICATION_LOGS_ERRORS_TOTAL = Metrics.fullName("application_logs_errors_total");
    static final String APPLICATION_LOGS_WARNINGS_TOTAL = Metrics.fullName("application_logs_warnings_total");
    static final String APPLICATION_LOGS_ERROR_TIMESTAMP = Metrics.fullName("application_logs_error_timestamp_seconds");
    static final String APPLICATION_LOGS_WARNING_TIMESTAMP =
            Metrics.fullName("application_logs_warning_timestamp_seconds");
    private final LogsConverter logsConverter;
    private final LogsRepository logsRepository;
    private final Counter logsErrorsCounter;
    private final Counter logsWarningsCounter;
    private final Gauges gauges;
    private final Clock clock;

    public LogsService(LogsConverter logsConverter,
                       LogsRepository logsRepository,
                       MeterRegistry meterRegistry,
                       Clock clock) {
        this.logsConverter = logsConverter;
        this.logsRepository = logsRepository;

        logsErrorsCounter = meterRegistry.counter(APPLICATION_LOGS_ERRORS_TOTAL);
        logsWarningsCounter = meterRegistry.counter(APPLICATION_LOGS_WARNINGS_TOTAL);

        logsErrorsCounter.increment(0);
        logsWarningsCounter.increment(0);

        gauges = new Gauges(meterRegistry);

        this.clock = clock;
    }

    public void add(List<LogData> logs) {
        var records = logs.stream()
                .map(l -> {
                    var r = logsConverter.converted(l);
                    setLogTimestampMetric(r.source(), r.application(), r.instanceId(), r.level());
                    return r;
                })
                .toList();

        logsRepository.store(records);
    }

    private void setLogTimestampMetric(String source,
                                       String application,
                                       String instanceId,
                                       ApplicationLogLevel logLevel) {
        if (logLevel == ApplicationLogLevel.ERROR) {
            logsErrorsCounter.increment();
            setLogTimestampMetric(APPLICATION_LOGS_ERROR_TIMESTAMP,
                    source, application, instanceId);
        } else if (logLevel == ApplicationLogLevel.WARNING) {
            logsWarningsCounter.increment();
            setLogTimestampMetric(APPLICATION_LOGS_WARNING_TIMESTAMP,
                    source, application, instanceId);
        }
    }

    private void setLogTimestampMetric(String metricName,
                                       String source,
                                       String application,
                                       String instanceId) {
        var tags = Tags.of(Metrics.applicationLabels(source, application, instanceId));
        gauges.updateValue(metricName, tags,
                Metrics.secondsTimestamp(clock.instant()));
    }
}
