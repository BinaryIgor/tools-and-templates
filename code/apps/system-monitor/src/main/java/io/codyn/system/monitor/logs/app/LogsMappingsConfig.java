package io.codyn.system.monitor.logs.app;

import io.codyn.system.monitor.logs.domain.model.ApplicationLogMapping;
import io.codyn.system.monitor.logs.domain.model.LogMapping;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Collection;

@ConfigurationProperties(prefix = "logs-mappings")
public record LogsMappingsConfig(Collection<ApplicationLogMapping> applications,
                                 LogMapping defaultMapping) {
}
