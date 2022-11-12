package com.igor101.system.monitor.logs.core;

import com.igor101.system.monitor.logs.core.model.LogRecord;

import java.util.List;

public interface LogsRepository {
    void store(List<LogRecord> logs);
}
