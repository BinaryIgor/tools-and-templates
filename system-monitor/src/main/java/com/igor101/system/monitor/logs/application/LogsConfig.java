package com.igor101.system.monitor.logs.application;

import com.igor101.system.monitor.logs.core.LogsConverter;
import com.igor101.system.monitor.logs.core.LogsService;
import com.igor101.system.monitor.logs.infrastructure.InMemoryLogsRepository;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(LogMappingsConfig.class)
public class LogsConfig {

    @Bean
    public LogsConverter logsConverter(LogMappingsConfig config) {
        return new LogsConverter(config.applications(), config.defaultMapping());
    }

    //TODO: real repo
    @Bean
    public LogsService logsService(LogsConverter logsConverter,
                                   MeterRegistry meterRegistry) {
        var repository = new InMemoryLogsRepository();

        return new LogsService(logsConverter, repository, meterRegistry);
    }
}
