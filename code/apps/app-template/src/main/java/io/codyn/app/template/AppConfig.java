package io.codyn.app.template;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.codyn.app.template._shared.app.BadRequestsInterceptor;
import io.codyn.app.template._shared.app.EmailModuleProvider;
import io.codyn.app.template._shared.app.SpringEventPublisher;
import io.codyn.email.factory.EmailFactory;
import io.codyn.email.server.EmailServer;
import io.codyn.email.server.PostmarkEmailServer;
import io.codyn.email.server.ToConsoleEmailServer;
import io.codyn.json.JsonMapper;
import io.codyn.sqldb.core.DSLContextFactory;
import io.codyn.sqldb.core.DSLContextProvider;
import io.codyn.sqldb.core.SqlTransactions;
import io.codyn.types.EventPublisher;
import io.codyn.types.Transactions;
import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.SpringProperties;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.time.Clock;

@Configuration
@EnableConfigurationProperties(EmailConfig.class)
public class AppConfig implements WebMvcConfigurer {

    @Bean
    public Clock clock() {
        return Clock.systemUTC();
    }

    @Bean
    public ObjectMapper objectMapper() {
        return JsonMapper.MAPPER;
    }

    @Bean
    public EventPublisher eventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        return new SpringEventPublisher(applicationEventPublisher);
    }

    @Bean
    public EmailServer emailServer(EmailConfig emailConfig) {
        if (emailConfig.fakeServer()) {
            return new ToConsoleEmailServer();
        }
        return new PostmarkEmailServer(emailConfig.postmarkApiToken());
    }

    @Bean
    public EmailFactory emailFactory(EmailConfig emailConfig) {
        return EmailModuleProvider.factory(emailConfig.templatesDir());
    }

    @Bean
    public DSLContext dslContext(@Value("${spring.datasource.url}") String jdbcUrl,
                                 @Value("${spring.datasource.username}") String username,
                                 @Value("${spring.datasource.password}") String password) {
        //TODO: support reading password from file!
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

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new BadRequestsInterceptor());
    }


    //TODO: revise!
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("localhost", "codyn.io");
    }
}
