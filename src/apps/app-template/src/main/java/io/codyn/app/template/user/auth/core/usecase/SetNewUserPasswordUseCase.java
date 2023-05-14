package io.codyn.app.template.user.auth.core.usecase;

import io.codyn.app.template._common.core.validator.FieldValidator;
import io.codyn.app.template.user.auth.core.model.SetNewPasswordCommand;
import io.codyn.app.template.user.common.core.ActivationTokenConsumer;
import io.codyn.app.template.user.common.core.PasswordHasher;
import io.codyn.app.template._common.core.model.ActivationTokenType;
import io.codyn.app.template.user.common.core.repository.UserUpdateRepository;

public class SetNewUserPasswordUseCase {

    private final ActivationTokenConsumer activationTokenConsumer;
    private final UserUpdateRepository userUpdateRepository;
    private final PasswordHasher passwordHasher;


    public SetNewUserPasswordUseCase(ActivationTokenConsumer activationTokenConsumer,
                                     UserUpdateRepository userUpdateRepository,
                                     PasswordHasher passwordHasher) {
        this.activationTokenConsumer = activationTokenConsumer;
        this.userUpdateRepository = userUpdateRepository;
        this.passwordHasher = passwordHasher;
    }

    public void handle(SetNewPasswordCommand command) {
        FieldValidator.validatePassword(command.password());

        activationTokenConsumer.consume(command.token(), ActivationTokenType.PASSWORD_RESET,
                userId -> {
                    var hashedPassword = passwordHasher.hash(command.password());
                    userUpdateRepository.updatePassword(userId, hashedPassword);
                });
    }
}
