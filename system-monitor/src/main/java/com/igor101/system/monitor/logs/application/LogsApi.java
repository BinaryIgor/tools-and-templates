package com.igor101.system.monitor.logs.application;

import java.util.List;

public record LogsApi(String source,
                      List<LogApi> logs) {
}
