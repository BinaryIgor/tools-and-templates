package io.codyn.app.template.user.domain.event;

import io.codyn.app.template.user.api.event.UserCreatedEvent;
import io.codyn.app.template.user.domain.UserEmailComponent;
import io.codyn.app.template.user.domain.repository.ActivationTokenRepository;
import io.codyn.commons.types.EventHandler;
import org.springframework.stereotype.Component;

@Component
public class UserCreatedEventHandler implements EventHandler<UserCreatedEvent> {

    private final UserEmailComponent emailComponent;
    private final ActivationTokenRepository activationTokenRepository;

    public UserCreatedEventHandler(UserEmailComponent emailComponent,
                                   ActivationTokenRepository activationTokenRepository) {
        this.emailComponent = emailComponent;
        this.activationTokenRepository = activationTokenRepository;
    }

    @Override
    public void handle(UserCreatedEvent event) {
        //TODO...

    }
}
