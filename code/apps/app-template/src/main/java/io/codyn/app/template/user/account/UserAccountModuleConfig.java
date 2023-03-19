package io.codyn.app.template.user.account;

import io.codyn.app.template.user.account.core.usecase.ChangeUserEmailUseCase;
import io.codyn.app.template.user.account.core.usecase.ConfirmUserEmailChangeUseCase;
import io.codyn.app.template.user.account.core.usecase.UpdateUserPasswordUseCase;
import io.codyn.app.template.user.common.core.ActivationTokenConsumer;
import io.codyn.app.template.user.common.core.ActivationTokens;
import io.codyn.app.template.user.common.core.PasswordHasher;
import io.codyn.app.template.user.common.core.UserEmailSender;
import io.codyn.app.template.user.common.core.repository.UserRepository;
import io.codyn.app.template.user.common.core.repository.UserUpdateRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UserAccountModuleConfig {

    @Bean
    ChangeUserEmailUseCase changeUserEmailUseCase(UserRepository userRepository,
                                                  ActivationTokens activationTokens,
                                                  UserEmailSender emailSender) {
        return new ChangeUserEmailUseCase(userRepository, activationTokens, emailSender);
    }

    @Bean
    ConfirmUserEmailChangeUseCase confirmUserEmailChangeUseCase(ActivationTokenConsumer activationTokenConsumer,
                                                                UserUpdateRepository userUpdateRepository) {
        return new ConfirmUserEmailChangeUseCase(activationTokenConsumer, userUpdateRepository);
    }

    @Bean
    UpdateUserPasswordUseCase updateUserPasswordUseCase(UserRepository userRepository,
                                                        UserUpdateRepository userUpdateRepository,
                                                        PasswordHasher passwordHasher) {
        return new UpdateUserPasswordUseCase(userRepository, userUpdateRepository, passwordHasher);
    }
}
