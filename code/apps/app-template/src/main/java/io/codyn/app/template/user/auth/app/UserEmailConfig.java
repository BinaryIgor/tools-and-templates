package io.codyn.app.template.user.auth.app;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.email.user")
public record UserEmailConfig(String userActivationUrl,
                              String signUpUrl,
                              String emailChangeConfirmationUrl,
                              String passwordResetUrl,
                              String newPasswordUrl) {
}
