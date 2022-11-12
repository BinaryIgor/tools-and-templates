package com.igor101.system.monitor.logs.infrastructure;

import com.igor101.system.monitor.logs.core.LogsRepository;
import com.igor101.system.monitor.logs.core.model.LogRecord;

import java.util.List;

public class InMemoryLogsRepository implements LogsRepository {

    @Override
    public void store(List<LogRecord> logs) {
        System.out.printf("Should store %d logs...\n", logs.size());
    }
}
