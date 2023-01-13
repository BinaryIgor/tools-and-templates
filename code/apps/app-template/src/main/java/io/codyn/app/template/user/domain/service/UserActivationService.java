package io.codyn.app.template.user.domain.service;

import io.codyn.app.template._shared.domain.model.UserState;
import io.codyn.app.template.user.domain.component.ActivationTokenConsumer;
import io.codyn.app.template.user.domain.model.activation.ActivationTokenType;
import io.codyn.app.template.user.domain.repository.UserUpdateRepository;
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
