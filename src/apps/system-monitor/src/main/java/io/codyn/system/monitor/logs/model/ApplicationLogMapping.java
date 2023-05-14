package io.codyn.system.monitor.logs.model;

import java.util.List;

public record ApplicationLogMapping(List<String> supportedApplicationsKeywords,
                                    LogMapping mapping) {
}
