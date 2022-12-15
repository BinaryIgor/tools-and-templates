package com.igor101.system.monitor.logs.core;

import com.igor101.system.monitor.common.Metrics;
import com.igor101.system.monitor.logs.core.model.ApplicationLogLevel;
import com.igor101.system.monitor.logs.core.model.LogData;
import io.micrometer.core.instrument.MeterRegistry;

import java.util.List;

public class LogsService {

    static final String APPLICATION_LOGS_ERRORS_TOTAL_METRIC = Metrics.fullName("application_logs_errors_total");
    static final String APPLICATION_LOGS_WARNINGS_TOTAL_METRIC = Metrics.fullName("application_logs_warnings_total");
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

    public void add(List<LogData> logs) {
        var records = logs.stream()
                .map(l -> {
                    var r = logsConverter.converted(l);
                    updateLogsMetrics(r.source(), r.application(), r.instanceId(), r.level());
                    return r;
                })
                .toList();

        logsRepository.store(records);
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
        meterRegistry.counter(metricName, Metrics.applicationLabels(source, application, instanceId))
                .increment();
    }
}
