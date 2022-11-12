package com.igor101.system.monitor.logs.core.model;

import java.util.List;

public record ApplicationLogMapping(List<String> supportedApplicationsKeywords,
                                    LogMapping mapping) {
}
