package io.codyn.app.template;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import io.codyn.app.template._shared.app.SpringEventPublisher;
import io.codyn.app.template._shared.domain.event.EventPublisher;
import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;
import org.jooq.impl.DefaultConfiguration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {

    @Bean
    public EventPublisher eventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        return new SpringEventPublisher(applicationEventPublisher);
    }

    //TODO: transactions...
    @Bean
    public DSLContext dslContext(@Value("${spring.datasource.url}") String jdbcUrl,
                                 @Value("${spring.datasource.username}") String username,
                                 @Value("${spring.datasource.password}") String password) {
        var config = new HikariConfig();
        config.setJdbcUrl(jdbcUrl);
        config.setUsername(username);
        config.setPassword(password);

        return DSL.using(new DefaultConfiguration()
                .set(new HikariDataSource(config))
                .set(SQLDialect.POSTGRES));
    }
}
