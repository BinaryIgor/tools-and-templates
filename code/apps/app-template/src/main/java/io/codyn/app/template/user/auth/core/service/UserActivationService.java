package io.codyn.app.template.user.auth.core.service;

import io.codyn.app.template._common.core.model.UserState;
import io.codyn.app.template.user.auth.core.repository.UserUpdateRepository;
import io.codyn.app.template.user.common.core.ActivationTokenConsumer;
import io.codyn.app.template.user.common.core.model.ActivationTokenType;
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
