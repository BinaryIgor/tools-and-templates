package io.codyn.app.template.user.app;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.email.user")
public record UserEmailConfig(String userActivationUrl,
                              String signUpUrl,
                              String emailChangeConfirmationUrl,
                              String passwordResetUrl,
                              String newPasswordUrl) {
}
