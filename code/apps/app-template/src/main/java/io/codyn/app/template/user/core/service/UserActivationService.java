package io.codyn.app.template.user.core.service;

import io.codyn.app.template._shared.core.model.UserState;
import io.codyn.app.template.user.core.component.ActivationTokenConsumer;
import io.codyn.app.template.user.core.model.activation.ActivationTokenType;
import io.codyn.app.template.user.core.repository.UserUpdateRepository;
import org.springframework.stereotype.Service;

@Service
public class UserActivationService {

    private final ActivationTokenConsumer activationTokenConsumer;
    private final UserUpdateRepository userUpdateRepository;

    public UserActivationService(ActivationTokenConsumer activationTokenConsumer,
                                 UserUpdateRepository userUpdateRepository) {
        this.activationTokenConsumer = activationTokenConsumer;
        this.userUpdateRepository = userUpdateRepository;
    }


    //TODO: event for UserAuthDataCache
    public void activate(String activationToken) {
        activationTokenConsumer.consume(activationToken, ActivationTokenType.NEW_USER,
                userId -> userUpdateRepository.updateState(userId, UserState.ACTIVATED));
    }
}
