package com.igor101.system.monitor.logs.application;

import com.igor101.system.monitor.logs.core.LogsConverter;
import com.igor101.system.monitor.logs.core.LogsRepository;
import com.igor101.system.monitor.logs.core.LogsService;
import com.igor101.system.monitor.logs.infrastructure.FileLogsRepository;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;

@Configuration
@EnableConfigurationProperties({LogsMappingsConfig.class, LogsStorageConfig.class})
public class LogsConfig {

    @Bean
    public LogsConverter logsConverter(LogsMappingsConfig config) {
        return new LogsConverter(config.applications(), config.defaultMapping());
    }

    @Bean
    public LogsRepository logsRepository(LogsStorageConfig config, Clock clock) {
        return new FileLogsRepository(clock, config.filePath(), config.maxFileSize(), config.maxFiles());
    }

    @Bean
    public LogsCleaner logsCleaner(LogsRepository logsRepository) {
        return new LogsCleaner(logsRepository);
    }


    @Bean
    public LogsService logsService(LogsConverter logsConverter,
                                   LogsRepository logsRepository,
                                   MeterRegistry meterRegistry) {
        return new LogsService(logsConverter, logsRepository, meterRegistry);
    }
}
