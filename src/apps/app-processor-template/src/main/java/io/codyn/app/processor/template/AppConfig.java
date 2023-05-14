package io.codyn.app.processor.template;

import io.codyn.app.processor.template._common.PropertiesConverter;
import io.codyn.sqldb.core.DSLContextFactory;
import io.codyn.sqldb.core.DSLContextProvider;
import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;

@Configuration
public class AppConfig {

    @Bean
    Clock clock() {
        return Clock.systemUTC();
    }

    @Bean
    DSLContext dslContext(@Value("${spring.datasource.url}") String jdbcUrl,
                          @Value("${spring.datasource.username}") String username,
                          @Value("${spring.datasource.password}") String password) {
        return DSLContextFactory.newContext(jdbcUrl, username,
                PropertiesConverter.valueOrFromFile(password));
    }

    @Bean
    DSLContextProvider dslContextProvider(DSLContext context) {
        return new DSLContextProvider(context);
    }
}
