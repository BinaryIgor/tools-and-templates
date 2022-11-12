package com.igor101.system.monitor.logs.application;

import com.igor101.system.monitor.logs.core.LogsConverter;
import com.igor101.system.monitor.logs.core.LogsRepository;
import com.igor101.system.monitor.logs.core.LogsService;
import com.igor101.system.monitor.logs.infrastructure.FileLogsRepository;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.File;

@Configuration
@EnableConfigurationProperties({LogMappingsConfig.class, LogsStorageConfig.class})
public class LogsConfig {

    @Bean
    public LogsConverter logsConverter(LogMappingsConfig config) {
        return new LogsConverter(config.applications(), config.defaultMapping());
    }

    @Bean
    public LogsRepository logsRepository(LogsStorageConfig config) {
        return new FileLogsRepository(new File(config.filePath()), 3_000_000);
    }

    @Bean
    public LogsService logsService(LogsConverter logsConverter,
                                   LogsRepository logsRepository,
                                   MeterRegistry meterRegistry) {
        return new LogsService(logsConverter, logsRepository, meterRegistry);
    }
}
