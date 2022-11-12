package com.igor101.system.monitor.logs.application;

import com.igor101.system.monitor.logs.core.model.LogData;

public record LogApi(String containerName,
                     String containerId,
                     long fromTimestamp,
                     long toTimestamp,
                     String log) {

    public LogData toLogData(String source) {
        return new LogData(source, containerName, containerId, fromTimestamp,
                toTimestamp, log);
    }
}
