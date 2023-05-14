package io.codyn.system.monitor.logs;

import io.codyn.system.monitor.logs.model.ApplicationLogMapping;
import io.codyn.system.monitor.logs.model.LogMapping;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Collection;

@ConfigurationProperties("logs-mappings")
public record LogsMappingsConfig(Collection<ApplicationLogMapping> applications,
                                 LogMapping defaultMapping) {
}
