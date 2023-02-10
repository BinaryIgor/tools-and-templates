package io.codyn.app.template.user;

import io.codyn.app.template.EmailConfig;
import io.codyn.app.template._common.core.AfterDelayCacheEnabler;
import io.codyn.app.template.user.auth.app.UserEmailConfig;
import io.codyn.app.template.user.common.core.UserEmailSender;
import io.codyn.app.template.user.common.core.cache.CacheableUserAuthDataRepository;
import io.codyn.app.template.user.common.infra.SqlUserAuthDataRepository;
import io.codyn.email.factory.EmailFactory;
import io.codyn.email.server.EmailServer;
import io.codyn.sqldb.core.DSLContextProvider;
import io.codyn.tools.CacheFactory;
import org.springframework.beans.factory.annotation.Value;
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

    @Bean
    public CacheableUserAuthDataRepository userAuthDataRepository(DSLContextProvider contextProvider,
                                                                  @Value("${app.cache.user-auth.time-to-live}") long timeToLive,
                                                                  @Value("${app.cache.user-auth.max-entries}") int maxEntries) {
        var baseRepository = new SqlUserAuthDataRepository(contextProvider);

        return new CacheableUserAuthDataRepository(baseRepository,
                CacheFactory.newCache(maxEntries, timeToLive),
                new AfterDelayCacheEnabler());
    }
}