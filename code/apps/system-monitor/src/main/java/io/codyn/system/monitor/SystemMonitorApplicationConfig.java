package io.codyn.system.monitor;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.codyn.json.JsonMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;

@Configuration
public class SystemMonitorApplicationConfig {

    @Bean
    public Clock clock() {
        return Clock.systemUTC();
    }

    @Bean
    public ObjectMapper objectMapper() {
        return JsonMapper.MAPPER;
    }
}
