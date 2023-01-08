package io.codyn.app.template.auth;

import io.codyn.app.template._shared.app.StringBytesMapper;
import io.codyn.app.template.auth.api.UserAuthDataRepository;
import io.codyn.app.template.auth.app.JwtConfig;
import io.codyn.app.template.auth.app.SecurityEndpoints;
import io.codyn.app.template.auth.app.SecurityRules;
import io.codyn.app.template.auth.domain.AuthTokenComponent;
import io.codyn.app.template.auth.domain.JwtAuthTokenComponent;
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
        var componentConfig = new JwtAuthTokenComponent.Config(
                config.issuer(),
                StringBytesMapper.bytesFromString(config.tokenKey()),
                config.accessTokenDuration(),
                config.refreshTokenDuration(),
                clock);

        return new JwtAuthTokenComponent(authDataRepository, componentConfig);
    }
}
