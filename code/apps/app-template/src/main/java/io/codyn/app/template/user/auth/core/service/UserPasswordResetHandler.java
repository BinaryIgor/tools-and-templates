package io.codyn.app.template.user.auth.core.service;

import io.codyn.app.template._common.core.validator.FieldValidator;
import io.codyn.app.template.user.auth.core.exception.UserExceptions;
import io.codyn.app.template.user.auth.core.repository.UserRepository;
import io.codyn.app.template.user.common.core.ActivationTokenFactory;
import io.codyn.app.template.user.common.core.ActivationTokenRepository;
import io.codyn.app.template.user.common.core.UserEmailComponent;
import io.codyn.app.template.user.common.core.model.EmailUser;
import org.springframework.stereotype.Service;

@Service
//TODO: tests
public class UserPasswordResetHandler {

    private final UserRepository userRepository;
    private final ActivationTokenRepository activationTokenRepository;
    private final ActivationTokenFactory activationTokenFactory;
    private final UserEmailComponent emailComponent;

    public UserPasswordResetHandler(UserRepository userRepository,
                                    ActivationTokenRepository activationTokenRepository,
                                    ActivationTokenFactory activationTokenFactory,
                                    UserEmailComponent emailComponent) {
        this.userRepository = userRepository;
        this.activationTokenRepository = activationTokenRepository;
        this.activationTokenFactory = activationTokenFactory;
        this.emailComponent = emailComponent;
    }

    public void handle(String email) {
        FieldValidator.isEmailValid(email);

        var user = userRepository.ofEmail(email)
                .orElseThrow(() -> UserExceptions.userOfEmailNotFound(email));

        var resetToken = activationTokenFactory.passwordReset(user.id());

        activationTokenRepository.save(resetToken);

        emailComponent.sendAccountActivation(new EmailUser(user.name(), user.email()), resetToken.token());
    }
}
