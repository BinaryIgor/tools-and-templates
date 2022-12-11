package com.igor101.system.monitor.alerts.application;

import java.util.Map;

public record Alert(String status,
                    Map<String, String> labels,
                    Map<String, String> annotations) {
}
