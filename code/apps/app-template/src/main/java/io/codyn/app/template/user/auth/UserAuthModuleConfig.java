package io.codyn.app.template.user.auth;

import io.codyn.app.template.auth.api.AuthClient;
import io.codyn.app.template.user.auth.core.repository.UserDeleteRepository;
import io.codyn.app.template.user.auth.core.usecase.*;
import io.codyn.app.template.user.auth.infra.SqlUserRepository;
import io.codyn.app.template.user.common.core.ActivationTokenConsumer;
import io.codyn.app.template.user.common.core.ActivationTokens;
import io.codyn.app.template.user.common.core.PasswordHasher;
import io.codyn.app.template.user.common.core.UserEmailSender;
import io.codyn.app.template.user.common.core.repository.ActivationTokenRepository;
import io.codyn.app.template.user.common.core.repository.UserAuthRepository;
import io.codyn.app.template.user.common.core.repository.UserRepository;
import io.codyn.app.template.user.common.core.repository.UserUpdateRepository;
import io.codyn.sqldb.core.DSLContextProvider;
import io.codyn.types.Transactions;
import io.codyn.types.event.LocalPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.time.Clock;
import java.time.Duration;

@Configuration
public class UserAuthModuleConfig {

    @Bean
    //Primary needed because of many interfaces impl
    @Primary
    SqlUserRepository userRepository(DSLContextProvider contextProvider) {
        return new SqlUserRepository(contextProvider);
    }

    @Bean
    ActivateUserUseCase activateUserUseCase(ActivationTokenConsumer activationTokenConsumer,
                                            UserUpdateRepository userUpdateRepository,
                                            LocalPublisher localPublisher) {
        return new ActivateUserUseCase(activationTokenConsumer, userUpdateRepository, localPublisher);
    }

    @Bean
    CreateUserUseCase createUserUseCase(UserRepository userRepository,
                                        PasswordHasher passwordHasher,
                                        ActivationTokens activationTokens,
                                        UserEmailSender emailSender,
                                        Transactions transactions) {
        return new CreateUserUseCase(userRepository, passwordHasher, activationTokens,
                emailSender, transactions);
    }

    @Bean
    DeleteNotActivatedUsersUseCase deleteNotActivatedUsersUseCase(UserDeleteRepository userDeleteRepository,
                                                                  Clock clock) {
        return new DeleteNotActivatedUsersUseCase(userDeleteRepository, Duration.ofMinutes(15), clock);
    }

    @Bean
    RefreshUserAuthTokensUseCase refreshUserAuthTokensUseCase(AuthClient authClient) {
        return new RefreshUserAuthTokensUseCase(authClient);
    }

    @Bean
    ResendUserAccountActivationTokenUseCase resendUserAccountActivationTokenUseCase(UserRepository userRepository,
                                                                                    ActivationTokenRepository activationTokenRepository,
                                                                                    UserEmailSender emailSender) {
        return new ResendUserAccountActivationTokenUseCase(userRepository, activationTokenRepository, emailSender);
    }

    @Bean
    ResetUserPasswordUseCase resetUserPasswordUseCase(UserRepository userRepository,
                                                      ActivationTokens activationTokens,
                                                      UserEmailSender emailSender) {
        return new ResetUserPasswordUseCase(userRepository, activationTokens, emailSender);
    }

    @Bean
    SetNewUserPasswordUseCase setNewUserPasswordUseCase(ActivationTokenConsumer activationTokenConsumer,
                                                        UserUpdateRepository userUpdateRepository,
                                                        PasswordHasher passwordHasher) {
        return new SetNewUserPasswordUseCase(activationTokenConsumer, userUpdateRepository, passwordHasher);
    }

    @Bean
    SignInFirstStepUseCase signInFirstStepUseCase(AuthClient authClient,
                                                  UserRepository userRepository,
                                                  UserAuthRepository userAuthRepository,
                                                  PasswordHasher passwordHasher) {
        return new SignInFirstStepUseCase(authClient, userRepository, userAuthRepository, passwordHasher);
    }
}
