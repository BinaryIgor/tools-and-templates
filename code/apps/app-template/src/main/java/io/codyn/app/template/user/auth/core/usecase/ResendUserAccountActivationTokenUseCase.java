package io.codyn.app.template.user.auth.core.usecase;

import io.codyn.app.template._common.core.validator.FieldValidator;
import io.codyn.app.template.user.common.core.ActivationTokenRepository;
import io.codyn.app.template.user.common.core.UserEmailSender;
import io.codyn.app.template.user.common.core.UserExceptions;
import io.codyn.app.template.user.common.core.model.ActivationTokenId;
import io.codyn.app.template.user.common.core.model.EmailUser;
import io.codyn.app.template.user.common.core.repository.UserRepository;
import org.springframework.stereotype.Component;

@Component
//TODO: tests!
public class ResendUserAccountActivationTokenUseCase {

    private final UserRepository userRepository;
    private final ActivationTokenRepository activationTokenRepository;
    private final UserEmailSender emailSender;

    public ResendUserAccountActivationTokenUseCase(UserRepository userRepository,
                                                   ActivationTokenRepository activationTokenRepository,
                                                   UserEmailSender emailSender) {
        this.userRepository = userRepository;
        this.activationTokenRepository = activationTokenRepository;
        this.emailSender = emailSender;
    }

    public void handle(String email) {
        FieldValidator.validateEmail(email);

        var user = userRepository.ofEmail(email)
                .orElseThrow(() -> UserExceptions.userOfEmailNotFound(email));

        var activationTokenId = ActivationTokenId.ofNewUser(user.id());
        var activationToken = activationTokenRepository.ofId(activationTokenId)
                .orElseThrow(() -> UserExceptions.activationTokenNotFound(activationTokenId));

        emailSender.sendAccountActivation(new EmailUser(user.name(), email),
                activationToken.token());
    }
}
