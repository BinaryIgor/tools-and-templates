package io.codyn.system.monitor.logs.domain;

import io.codyn.system.monitor.logs.domain.model.LogRecord;

import java.util.List;

public interface LogsRepository {

    void store(List<LogRecord> logs);

    void clear();
}
