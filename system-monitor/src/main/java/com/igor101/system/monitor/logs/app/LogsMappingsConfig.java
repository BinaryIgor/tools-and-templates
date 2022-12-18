package com.igor101.system.monitor.logs.app;

import com.igor101.system.monitor.logs.domain.model.ApplicationLogMapping;
import com.igor101.system.monitor.logs.domain.model.LogMapping;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;

import java.util.Collection;

@ConfigurationProperties(prefix = "logs-mappings")
@ConstructorBinding
public record LogsMappingsConfig(Collection<ApplicationLogMapping> applications,
                                 LogMapping defaultMapping) {
}
