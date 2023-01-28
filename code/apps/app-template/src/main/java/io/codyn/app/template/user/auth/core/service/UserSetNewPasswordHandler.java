package io.codyn.app.template.user.auth.core.service;

import io.codyn.app.template._common.core.validator.FieldValidator;
import io.codyn.app.template.user.auth.core.model.NewPasswordRequest;
import io.codyn.app.template.user.auth.core.repository.UserUpdateRepository;
import io.codyn.app.template.user.common.core.ActivationTokenConsumer;
import io.codyn.app.template.user.common.core.model.ActivationTokenType;
import org.springframework.stereotype.Service;

@Service
public class UserSetNewPasswordHandler {

    private final ActivationTokenConsumer activationTokenConsumer;
    private final UserUpdateRepository userUpdateRepository;

    public UserSetNewPasswordHandler(ActivationTokenConsumer activationTokenConsumer,
                                     UserUpdateRepository userUpdateRepository) {
        this.activationTokenConsumer = activationTokenConsumer;
        this.userUpdateRepository = userUpdateRepository;
    }

    public void handle(NewPasswordRequest request) {
        FieldValidator.isPasswordValid(request.password());

        activationTokenConsumer.consume(request.token(), ActivationTokenType.PASSWORD_RESET,
                userId -> userUpdateRepository.updatePassword(userId, request.password()));
    }
}
