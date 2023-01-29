package io.codyn.app.template.user.auth.app;

import io.codyn.app.template.EmailConfig;
import io.codyn.app.template.user.common.core.UserEmailSender;
import io.codyn.email.factory.EmailFactory;
import io.codyn.email.server.EmailServer;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(UserEmailConfig.class)
public class UserModuleConfig {

    @Bean
    public UserEmailSender userEmailSender(EmailFactory emailFactory,
                                           EmailServer emailServer,
                                           EmailConfig emailConfig,
                                           UserEmailConfig userEmailConfig) {
        return new UserEmailSender(emailFactory, emailServer,
                new UserEmailSender.Config(emailConfig.frontendDomain(),
                        emailConfig.fromEmail(),
                        userEmailConfig.userActivationUrl(),
                        userEmailConfig.signUpUrl(),
                        userEmailConfig.emailChangeConfirmationUrl(),
                        userEmailConfig.passwordResetUrl(),
                        userEmailConfig.newPasswordUrl()));
    }
}
