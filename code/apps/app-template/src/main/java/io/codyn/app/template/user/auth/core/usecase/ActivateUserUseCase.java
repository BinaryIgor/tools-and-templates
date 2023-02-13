package io.codyn.app.template.user.auth.core.usecase;

import io.codyn.app.template._common.core.model.UserState;
import io.codyn.app.template.user.api.UserStateChangedEvent;
import io.codyn.app.template.user.common.core.ActivationTokenConsumer;
import io.codyn.app.template.user.common.core.model.ActivationTokenType;
import io.codyn.app.template.user.common.core.repository.UserUpdateRepository;
import io.codyn.types.EventHandler;
import org.springframework.stereotype.Component;

@Component
public class ActivateUserUseCase {

    private final ActivationTokenConsumer activationTokenConsumer;
    private final UserUpdateRepository userUpdateRepository;
    private final EventHandler<UserStateChangedEvent> userStateChangedEventHandler;

    public ActivateUserUseCase(ActivationTokenConsumer activationTokenConsumer,
                               UserUpdateRepository userUpdateRepository,
                               EventHandler<UserStateChangedEvent> userStateChangedEventHandler) {
        this.activationTokenConsumer = activationTokenConsumer;
        this.userUpdateRepository = userUpdateRepository;
        this.userStateChangedEventHandler = userStateChangedEventHandler;
    }

    public void handle(String activationToken) {
        activationTokenConsumer.consume(activationToken, ActivationTokenType.NEW_USER,
                userId -> {
                    userUpdateRepository.updateState(userId, UserState.ACTIVATED);
                    userStateChangedEventHandler.handle(new UserStateChangedEvent(userId, UserState.ACTIVATED));
                });
    }
}
