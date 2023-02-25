package io.codyn.app.template.user.auth.core.usecase;

import io.codyn.app.template._common.core.model.UserState;
import io.codyn.app.template.user.common.core.ActivationTokenConsumer;
import io.codyn.app.template.user.common.core.UserStateChangedEvent;
import io.codyn.app.template.user.common.core.model.ActivationTokenType;
import io.codyn.app.template.user.common.core.repository.UserUpdateRepository;
import io.codyn.types.event.LocalPublisher;
import org.springframework.stereotype.Component;

@Component
public class ActivateUserUseCase {

    private final ActivationTokenConsumer activationTokenConsumer;
    private final UserUpdateRepository userUpdateRepository;
    private final LocalPublisher localPublisher;

    public ActivateUserUseCase(ActivationTokenConsumer activationTokenConsumer,
                               UserUpdateRepository userUpdateRepository,
                               LocalPublisher localPublisher) {
        this.activationTokenConsumer = activationTokenConsumer;
        this.userUpdateRepository = userUpdateRepository;
        this.localPublisher = localPublisher;
    }

    public void handle(String activationToken) {
        activationTokenConsumer.consume(activationToken, ActivationTokenType.NEW_USER,
                userId -> {
                    userUpdateRepository.updateState(userId, UserState.ACTIVATED);
                    localPublisher.publish(new UserStateChangedEvent(userId, UserState.ACTIVATED));
                });
    }
}
