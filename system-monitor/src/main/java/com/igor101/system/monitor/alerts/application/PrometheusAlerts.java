package com.igor101.system.monitor.alerts.application;

import java.util.List;
import java.util.Map;

//https://prometheus.io/docs/alerting/latest/configuration/#webhook_config
public record PrometheusAlerts(String groupKey,
                               int truncatedAlerts,
                               String status,
                               Map<String, String> groupLabels,
                               Map<String, String> commonLabels,
                               String externalURL,
                               List<Alert> alerts) {
}
