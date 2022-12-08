package com.igor101.system.monitor.alerts.application;

import java.time.LocalDateTime;
import java.util.Map;

public record PrometheusAlert(String status,
                              Map<String, String> labels,
                              Map<String, String> annotations,
                              LocalDateTime startsAt,
                              LocalDateTime endsAt
//                              String generatorURL,
//                              String fingerprint
) {
}
