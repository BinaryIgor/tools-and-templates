package com.igor101.system.monitor.logs.application;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;

@ConfigurationProperties(prefix = "logs-storage")
@ConstructorBinding
public record LogsStorageConfig(String filePath) {

}
