package io.codyn.app.template;

import io.codyn.app.template._shared.app.JdbcTemplates;
import io.codyn.app.template._shared.app.SpringEventPublisher;
import io.codyn.app.template._shared.domain.event.EventPublisher;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

@Configuration
public class AppConfig {

    @Bean
    public EventPublisher eventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        return new SpringEventPublisher(applicationEventPublisher);
    }

    @Bean
    public JdbcTemplates jdbcTemplates(JdbcTemplate jdbcTemplate,
                                       NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        return new JdbcTemplates(jdbcTemplate, namedParameterJdbcTemplate);
    }
}
