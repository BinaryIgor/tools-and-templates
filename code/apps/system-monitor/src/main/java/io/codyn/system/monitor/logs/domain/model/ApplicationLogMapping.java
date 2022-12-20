package io.codyn.system.monitor.logs.domain.model;

import java.util.List;

public record ApplicationLogMapping(List<String> supportedApplicationsKeywords,
                                    LogMapping mapping) {
}
