package io.codyn.app.template.auth.app;

import io.codyn.app.template._shared.app.PropertiesConverter;
import io.codyn.app.template.auth.api.UserAuthDataRepository;
import io.codyn.app.template.auth.core.AuthTokenComponent;
import io.codyn.app.template.auth.core.JwtAuthTokenComponent;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;

@Configuration
@EnableConfigurationProperties(JwtConfig.class)
public class AuthModuleConfig {

    @Bean
    public SecurityRules securityRules() {
        return new SecurityRules(new SecurityRules.Predicates(
                SecurityEndpoints::isPublic,
                SecurityEndpoints::isUserOfStateAllowed,
                SecurityEndpoints::isAdmin));
    }

    @Bean
    public AuthTokenComponent authTokenComponent(UserAuthDataRepository authDataRepository,
                                                 JwtConfig config,
                                                 Clock clock) {
        var readTokenKey = PropertiesConverter.valueOrFromFile(config.tokenKey());
        var bytesTokenKey = PropertiesConverter.bytesFromString(readTokenKey);

        var componentConfig = new JwtAuthTokenComponent.Config(
                config.issuer(),
                bytesTokenKey,
                config.accessTokenDuration(),
                config.refreshTokenDuration(),
                clock);

        return new JwtAuthTokenComponent(authDataRepository, componentConfig);
    }
}
