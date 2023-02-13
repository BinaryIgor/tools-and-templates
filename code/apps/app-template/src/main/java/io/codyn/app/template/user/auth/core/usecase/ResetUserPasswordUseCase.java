package io.codyn.app.template.user.auth.core.usecase;

import io.codyn.app.template._common.core.validator.FieldValidator;
import io.codyn.app.template.user.common.core.ActivationTokens;
import io.codyn.app.template.user.common.core.UserEmailSender;
import io.codyn.app.template.user.common.core.UserExceptions;
import io.codyn.app.template.user.common.core.model.EmailUser;
import io.codyn.app.template.user.common.core.repository.UserRepository;
import org.springframework.stereotype.Component;

@Component
public class ResetUserPasswordUseCase {

    private final UserRepository userRepository;
    private final ActivationTokens activationTokens;
    private final UserEmailSender emailSender;


    public ResetUserPasswordUseCase(UserRepository userRepository,
                                    ActivationTokens activationTokens,
                                    UserEmailSender emailSender) {
        this.userRepository = userRepository;
        this.activationTokens = activationTokens;
        this.emailSender = emailSender;
    }

    public void handle(String email) {
        FieldValidator.validateEmail(email);

        var user = userRepository.ofEmail(email)
                .orElseThrow(() -> UserExceptions.userOfEmailNotFound(email));

        var resetToken = activationTokens.savePasswordReset(user.id());

        emailSender.sendPasswordReset(new EmailUser(user.name(), user.email()), resetToken.token());
    }

}
