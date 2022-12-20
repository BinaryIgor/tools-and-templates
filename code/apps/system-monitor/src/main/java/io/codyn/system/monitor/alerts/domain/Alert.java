package io.codyn.system.monitor.alerts.domain;

import java.util.Map;

public record Alert(String status,
                    Map<String, String> labels,
                    Map<String, String> annotations) {
}
