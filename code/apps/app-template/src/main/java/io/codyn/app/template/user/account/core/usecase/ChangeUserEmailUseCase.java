package io.codyn.app.template.user.account.core.usecase;

import io.codyn.app.template._common.core.email.Emails;
import io.codyn.app.template._common.core.exception.EmailNotReachableException;
import io.codyn.app.template._common.core.validator.FieldValidator;
import io.codyn.app.template.user.account.core.model.ChangeUserEmailCommand;
import io.codyn.app.template.user.common.core.ActivationTokens;
import io.codyn.app.template.user.common.core.UserEmailSender;
import io.codyn.app.template.user.common.core.UserExceptions;
import io.codyn.app.template.user.common.core.model.EmailUser;
import io.codyn.app.template.user.common.core.repository.UserRepository;
import org.springframework.stereotype.Component;

@Component
public class ChangeUserEmailUseCase {

    private final UserRepository userRepository;
    private final ActivationTokens activationTokens;
    private final UserEmailSender emailSender;

    public ChangeUserEmailUseCase(UserRepository userRepository,
                                  ActivationTokens activationTokens,
                                  UserEmailSender emailSender) {
        this.userRepository = userRepository;
        this.activationTokens = activationTokens;
        this.emailSender = emailSender;
    }

    public void handle(ChangeUserEmailCommand command) {
        validateEmail(command.email());

        var user = userRepository.ofId(command.id())
                .orElseThrow(() -> UserExceptions.userOfIdNotFound(command.id()));

        var activationToken = activationTokens.saveNewEmail(command.id(), command.email());

        var newEmailUser = new EmailUser(user.name(), command.email());
        var currentEmail = user.email();

        emailSender.sendEmailChange(newEmailUser, currentEmail, activationToken.token());
    }

    private void validateEmail(String email) {
        FieldValidator.validateEmail(email);
        if (!Emails.isReachable(email)) {
            throw new EmailNotReachableException(email);
        }
    }

}
