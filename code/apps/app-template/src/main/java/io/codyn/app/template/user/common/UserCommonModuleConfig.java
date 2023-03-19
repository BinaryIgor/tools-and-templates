package io.codyn.app.template.user.common;

import io.codyn.app.template.EmailConfig;
import io.codyn.app.template._common.core.AfterDelayCacheEnabler;
import io.codyn.app.template.user.auth.app.UserEmailConfig;
import io.codyn.app.template.user.common.core.*;
import io.codyn.app.template.user.common.core.cache.CacheableUserAuthDataRepository;
import io.codyn.app.template.user.common.core.repository.ActivationTokenRepository;
import io.codyn.app.template.user.common.core.repository.ActivationTokenStatusUpdateRepository;
import io.codyn.app.template.user.common.infra.SqlActivationTokenRepository;
import io.codyn.app.template.user.common.infra.SqlUserAuthDataRepository;
import io.codyn.email.factory.EmailFactory;
import io.codyn.email.server.EmailServer;
import io.codyn.email.server.PostmarkEmailStatusHandler;
import io.codyn.sqldb.core.DSLContextProvider;
import io.codyn.tools.CacheFactory;
import io.codyn.types.Transactions;
import io.codyn.types.event.LocalEvents;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;
import java.util.Map;

@Configuration
@EnableConfigurationProperties(UserEmailConfig.class)
public class UserCommonModuleConfig {

    @Bean
    UserEmailSender userEmailSender(EmailFactory emailFactory,
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
    CacheableUserAuthDataRepository userAuthDataRepository(DSLContextProvider contextProvider,
                                                           @Value("${app.cache.user-auth.time-to-live}") long timeToLive,
                                                           @Value("${app.cache.user-auth.max-entries}") int maxEntries,
                                                           LocalEvents localEvents) {
        var baseRepository = new SqlUserAuthDataRepository(contextProvider);

        return new CacheableUserAuthDataRepository(baseRepository,
                CacheFactory.newCache(maxEntries, timeToLive),
                new AfterDelayCacheEnabler(),
                localEvents);
    }

    @Bean
    SqlActivationTokenRepository activationTokenRepository(DSLContextProvider contextProvider) {
        return new SqlActivationTokenRepository(contextProvider);
    }

    @Bean
    ActivationTokenFactory activationTokenFactory(Clock clock) {
        return new ActivationTokenFactory(clock);
    }

    @Bean
    ActivationTokens activationTokens(ActivationTokenRepository activationTokenRepository,
                                      ActivationTokenFactory activationTokenFactory) {
        return new ActivationTokens(activationTokenRepository, activationTokenFactory);
    }

    @Bean
    ActivationTokenConsumer activationTokenConsumer(ActivationTokenRepository activationTokenRepository,
                                                    Transactions transactions) {
        return new ActivationTokenConsumer(activationTokenRepository, transactions);
    }

    @Bean
    PasswordHasher passwordHasher() {
        return new BcryptPasswordHasher();
    }

    @Bean
    UserEmailStatusHandler userEmailStatusHandler(
            ActivationTokenStatusUpdateRepository activationTokenStatusUpdateRepository) {
        return new UserEmailStatusHandler(activationTokenStatusUpdateRepository);
    }

    @Bean
    PostmarkEmailStatusHandler postmarkEmailStatusHandler(UserEmailStatusHandler emailStatusHandler) {
        return new PostmarkEmailStatusHandler(new PostmarkEmailStatusHandler.Actions() {
            @Override
            public void onBounce(Map<String, String> emailMetadata) {
                emailStatusHandler.handleBounce(emailMetadata);
            }

            @Override
            public void onDelivery(Map<String, String> emailMetadata) {
                emailStatusHandler.handleDelivery(emailMetadata);
            }
        });
    }
}
