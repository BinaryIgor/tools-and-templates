package com.igor101.system.monitor.logs.app;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;

@ConfigurationProperties(prefix = "logs-storage")
@ConstructorBinding
public record LogsStorageConfig(String filePath,
                                int maxFileSize,
                                int maxFiles) {

}
