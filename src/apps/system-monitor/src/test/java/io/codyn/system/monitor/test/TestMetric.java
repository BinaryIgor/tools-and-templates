package io.codyn.system.monitor.test;

import java.util.Map;

public record TestMetric(String name, Map<String, String> labels, String value) {
}
