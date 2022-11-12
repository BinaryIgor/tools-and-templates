package com.igor101.system.monitor.logs.core;

import com.igor101.system.monitor.logs.core.model.ApplicationLogLevel;
import com.igor101.system.monitor.logs.core.model.LogData;
import io.micrometer.core.instrument.MeterRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.util.List;

public class LogsService {

    static final String APPLICATION_LOGS_ERRORS_TOTAL_METRIC = "application_logs_errors_total";
    static final String APPLICATION_LOGS_WARNINGS_TOTAL_METRIC = "application_logs_warnings_total";
    static final String APPLICATION_LABEL = "monitored_application";
    static final String INSTANCE_ID_LABEL = "instance_id";
    static final String SOURCE_LABEL = "source";
    private static final Logger log = LoggerFactory.getLogger(LogsService.class);
    private final LogsConverter logsConverter;
    private final LogsRepository logsRepository;
    private final MeterRegistry meterRegistry;

    public LogsService(LogsConverter logsConverter,
                       LogsRepository logsRepository,
                       MeterRegistry meterRegistry) {
        this.logsConverter = logsConverter;
        this.logsRepository = logsRepository;
        this.meterRegistry = meterRegistry;
    }

    //TODO: global error handler
    public void handle(List<LogData> logs, Instant receivedTimestamp) {
        log.info("Have {} logs to handle...", logs.size());

        var records = logs.stream()
                .map(l -> {
                    var r = logsConverter.converted(l, receivedTimestamp);
                    updateLogsMetrics(r.source(), r.application(), r.instanceId(), r.level());
                    return r;
                })
                .toList();

        logsRepository.store(records);

        log.info("Logs handled");
    }

    private void updateLogsMetrics(String source,
                                   String application,
                                   String instanceId,
                                   ApplicationLogLevel logLevel) {
        if (logLevel == ApplicationLogLevel.ERROR) {
            increaseLogsMetricCounter(APPLICATION_LOGS_ERRORS_TOTAL_METRIC,
                    source, application, instanceId);
        } else if (logLevel == ApplicationLogLevel.WARNING) {
            increaseLogsMetricCounter(APPLICATION_LOGS_WARNINGS_TOTAL_METRIC,
                    source, application, instanceId);
        }
    }

    private void increaseLogsMetricCounter(String metricName,
                                           String source,
                                           String application,
                                           String instanceId) {
        meterRegistry.counter(metricName,
                        SOURCE_LABEL, source,
                        APPLICATION_LABEL, application,
                        INSTANCE_ID_LABEL, instanceId)
                .increment();
    }
}
