package io.codyn.system.monitor.logs.repository;

import io.codyn.system.monitor.logs.model.LogRecord;

import java.util.List;

public interface LogsRepository {

    void store(List<LogRecord> logs);

    void clear();
}
