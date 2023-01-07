package io.codyn.app.template;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.codyn.app.template._shared.app.SpringEventPublisher;
import io.codyn.commons.json.JsonMapper;
import io.codyn.commons.sqldb.core.DSLContextFactory;
import io.codyn.commons.sqldb.core.DSLContextProvider;
import io.codyn.commons.sqldb.core.SqlTransactions;
import io.codyn.commons.types.EventPublisher;
import io.codyn.commons.types.Transactions;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.time.Clock;

@Configuration
public class AppConfig {

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

    //TODO: customize
    @Profile("local")
    @Bean
    public OpenAPI customOpenAPI() {
//        new Paths()
//                .addPathItem("error", new PathItem()
//                        .)

        return new OpenAPI()
                .components(new Components()
                        .addSecuritySchemes("bearer-key",
                                new SecurityScheme().type(SecurityScheme.Type.HTTP).scheme("bearer")
                                        .bearerFormat("JWT")))
                .info(new Info()
                        .title("App Template API")
                        .version("1.0"));
//                .addTagsItem(new Tag()
//                        .name(ApiExceptionResponse.class.getSimpleName() + "-exceptions")
//                        .description("many errors ".repeat(100)));
    }

//    https://springdoc.org/v2/#can-i-customize-openapi-object-programmatically
//    @Profile("local")
//    @Bean
//    public OpenApiCustomizer customerGlobalHeaderOpenApiCustomizer(GroupedOpenApi openApi) {
//        return openApi -> {
//          //TODO customize!
//            System.out.println("Show, what we have!");
//            System.out.println(openApi);
//            System.out.println(openApi.getComponents());
//        };
//    }
}
