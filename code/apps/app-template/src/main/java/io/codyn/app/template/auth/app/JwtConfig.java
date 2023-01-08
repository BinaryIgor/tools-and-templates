package io.codyn.app.template.auth.app;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

@ConfigurationProperties(prefix = "app.jwt")
public record JwtConfig(String issuer,
                        String tokenKey,
                        Duration accessTokenDuration,
                        Duration refreshTokenDuration) {
}
