package io.codyn.app.template.user.auth.core.service;

import io.codyn.app.template._common.core.validator.FieldValidator;
import io.codyn.app.template.user.auth.core.exception.UserExceptions;
import io.codyn.app.template.user.auth.core.model.NewPasswordRequest;
import io.codyn.app.template.user.auth.core.repository.UserRepository;
import io.codyn.app.template.user.auth.core.repository.UserUpdateRepository;
import io.codyn.app.template.user.common.core.ActivationTokenConsumer;
import io.codyn.app.template.user.common.core.ActivationTokens;
import io.codyn.app.template.user.common.core.PasswordHasher;
import io.codyn.app.template.user.common.core.UserEmailSender;
import io.codyn.app.template.user.common.core.model.ActivationTokenType;
import io.codyn.app.template.user.common.core.model.EmailUser;
import org.springframework.stereotype.Service;

@Service
public class UserPasswordResetService {

    private final UserRepository userRepository;
    private final ActivationTokens activationTokens;
    private final ActivationTokenConsumer activationTokenConsumer;
    private final UserUpdateRepository userUpdateRepository;
    private final UserEmailSender emailSender;
    private final PasswordHasher passwordHasher;


    public UserPasswordResetService(UserRepository userRepository,
                                    ActivationTokens activationTokens,
                                    ActivationTokenConsumer activationTokenConsumer,
                                    UserUpdateRepository userUpdateRepository,
                                    UserEmailSender emailSender,
                                    PasswordHasher passwordHasher) {
        this.userRepository = userRepository;
        this.activationTokens = activationTokens;
        this.activationTokenConsumer = activationTokenConsumer;
        this.userUpdateRepository = userUpdateRepository;
        this.emailSender = emailSender;
        this.passwordHasher = passwordHasher;
    }

    public void resetPassword(String email) {
        FieldValidator.validateEmail(email);

        var user = userRepository.ofEmail(email)
                .orElseThrow(() -> UserExceptions.userOfEmailNotFound(email));

        var resetToken = activationTokens.savePasswordReset(user.id());

        emailSender.sendAccountActivation(new EmailUser(user.name(), user.email()), resetToken.token());
    }

    public void setNewPassword(NewPasswordRequest request) {
        FieldValidator.validatePassword(request.password());

        activationTokenConsumer.consume(request.token(), ActivationTokenType.PASSWORD_RESET,
                userId -> {
                    var hashedPassword = passwordHasher.hash(request.password());
                    userUpdateRepository.updatePassword(userId, hashedPassword);
                });
    }
}
