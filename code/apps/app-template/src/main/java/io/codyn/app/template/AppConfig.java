package io.codyn.app.template;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.codyn.app.template._shared.app.SpringEventPublisher;
import io.codyn.commons.json.JsonMapper;
import io.codyn.commons.sqldb.core.DSLContextFactory;
import io.codyn.commons.sqldb.core.DSLContextProvider;
import io.codyn.commons.sqldb.core.SqlTransactions;
import io.codyn.commons.types.EventPublisher;
import io.codyn.commons.types.Transactions;
import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {

    @Bean
    public ObjectMapper objectMapper() {
        return JsonMapper.MAPPER;
    }

    @Bean
    public EventPublisher eventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        return new SpringEventPublisher(applicationEventPublisher);
    }

    @Bean
    public DSLContext dslContext(@Value("${spring.datasource.url}") String jdbcUrl,
                                 @Value("${spring.datasource.username}") String username,
                                 @Value("${spring.datasource.password}") String password) {
        return DSLContextFactory.newContext(jdbcUrl, username, password);
    }

    @Bean
    public DSLContextProvider dslContextProvider(DSLContext context) {
        return new DSLContextProvider(context);
    }

    @Bean
    public Transactions transactions(DSLContextProvider contextProvider) {
        return new SqlTransactions(contextProvider);
    }
}
