package com.igor101.system.monitor.logs.application;

import com.igor101.system.monitor.logs.core.model.ApplicationLogMapping;
import com.igor101.system.monitor.logs.core.model.LogMapping;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;

import java.util.Collection;

@ConfigurationProperties(prefix = "log-mappings")
@ConstructorBinding
public record LogMappingsConfig(Collection<ApplicationLogMapping> applications,
                                LogMapping defaultMapping) {
}
